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
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;


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

    private RopValidator ropValidator = new DefaultRopValidator();

    private SecurityManager securityManager = new DefaultSecurityManager();

    private FormattingConversionService conversionService;

    private Validator validator;


    /**
     * 调用BOP服务方法
     *
     * @param bopServiceHandler
     * @param webRequest
     * @return
     */
    public RopResponse invokeServiceMethod(RopServiceContext context) {
        try {
            RopServiceHandler ropServiceHandler = context.getRopServiceHandler();
            MainError mainError = preProcess(context);
            if (mainError != null) {
                return new ErrorResponse(mainError);
            } else {
                if(logger.isDebugEnabled()){
                    logger.debug("执行"+ropServiceHandler.getHandler().getClass()+"."+ropServiceHandler.getHandlerMethod().getName());
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

    private MainError preProcess(RopServiceContext context) {
        BindingResult bindingResult = doBind(context.getWebRequest(), context.getRopServiceHandler().getRequestType());
        context.setAllErrors(bindingResult.getAllErrors());
        context.setRopRequest((RopRequest) bindingResult.getTarget());

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

    private BindingResult doBind(HttpServletRequest webRequest, Class<? extends RopRequest> requestType) {
        Object bindObject = BeanUtils.instantiateClass(requestType);

        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(bindObject, "bindObject");
        dataBinder.setConversionService(getConversionService());
        dataBinder.setValidator(getValidator());
        dataBinder.bind(webRequest);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

    private ConversionService getConversionService() {
        if (this.conversionService == null) {
            FormattingConversionServiceFactoryBean serviceFactoryBean = new FormattingConversionServiceFactoryBean();
            serviceFactoryBean.afterPropertiesSet();
            this.conversionService = serviceFactoryBean.getObject();
        }
        return this.conversionService;
    }

    private Validator getValidator() {
        if (this.validator == null) {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            localValidatorFactoryBean.afterPropertiesSet();
            this.validator = localValidatorFactoryBean;
        }
        return this.validator;
    }

}

