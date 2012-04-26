/**
 * 日    期：12-2-11
 */
package com.rop.impl;

import com.rop.*;
import com.rop.SecurityManager;
import com.rop.response.ErrorResponse;
import com.rop.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 *    调用<code>@BopServiceMethod</code>注解的BOP方法适配器
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationRopServiceMethodAdapter implements RopServiceMethodAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private RopValidator ropValidator ;

    private SecurityManager securityManager;

    public AnnotationRopServiceMethodAdapter(RopConfig ropConfig) {
        DefaultRopValidator defaultRopValidator = new DefaultRopValidator();
        defaultRopValidator.setAppSecretManager(ropConfig.getAppSecretManager());
        defaultRopValidator.setSessionChecker(ropConfig.getSessionChecker());
        this.ropValidator = defaultRopValidator;
        this.securityManager = ropConfig.getSecurityManager();
    }

    /**
     * 调用BOP服务方法
     *
     * @param bopServiceHandler
     * @param webRequest
     * @return
     */
    public RopResponse invokeServiceMethod(RopServiceContext context) {
        try {
            //分析上下文中的错误
            MainError mainError = paserMainError(context);

            if (mainError != null) {
                return new ErrorResponse(mainError);
            } else {
                RopServiceHandler ropServiceHandler = context.getRopServiceHandler();
                if (logger.isDebugEnabled()) {
                    logger.debug("执行" + ropServiceHandler.getHandler().getClass() + "." + ropServiceHandler.getHandlerMethod().getName());
                }
                if (ropServiceHandler.isHandlerMethodWithParameter()) {
                    return (RopResponse) ropServiceHandler.getHandlerMethod().invoke(
                            ropServiceHandler.getHandler(), context.getRopRequest());
                } else {
                    return (RopResponse) ropServiceHandler.getHandlerMethod().invoke(ropServiceHandler.getHandler());
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分析{@link RopServiceContext}中的错误，如果没有错误，返回null
     *
     * @param context
     * @return
     */
    private MainError paserMainError(RopServiceContext context) {

        //1.参数类型、缺失、格式、值的有效性检查
        MainError mainError = ropValidator.validate(context);

        //2.调用安全性检验
        if (mainError == null) {
            mainError = checkSecurity(context);
        }

        return mainError;
    }

    /**
     * 对目标方法的调用安全性进行检查
     *
     * @param context
     * @return
     */
    private MainError checkSecurity(RopServiceContext context) {
        RopRequest ropRequest = context.getRopRequest();
        if (!securityManager.isGranted(context)) {
            MainError mainError = SubErrors.getMainError(SubErrorType.ISV_INVALID_PERMISSION, ropRequest.getLocale());
            SubError subError = SubErrors.getSubError(SubErrorType.ISV_INVALID_PERMISSION.value(),
                    SubErrorType.ISV_INVALID_PERMISSION.value(),
                    ropRequest.getLocale());
            mainError.addSubError(subError);
            return mainError;
        } else {
            return null;
        }
    }

}

