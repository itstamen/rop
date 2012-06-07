/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.impl;

import com.rop.*;
import com.rop.config.SysparamNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <pre>
 *    构建{@link com.rop.RequestContext}实例
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServletRequestContextBuilder implements RequestContextBuilder {

    private FormattingConversionService conversionService;

    private Validator validator;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SimpleRequestContext buildRequestContext(RopContext ropContext, Object request,FormattingConversionService conversionService) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("请求对象必须是HttpServletRequest的类型");
        }

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        SimpleRequestContext reqeustContext = new SimpleRequestContext(ropContext);
        this.conversionService = conversionService;

        //设置请求对象及参数列表
        reqeustContext.setRawRequestObject(servletRequest);
        reqeustContext.setAllParams(getRequestParams(servletRequest));
        reqeustContext.setIp(servletRequest.getRemoteAddr());

        //设置服务的系统级参数
        reqeustContext.setMethod(servletRequest.getParameter(SysparamNames.getMethod()));
        reqeustContext.setVersion(servletRequest.getParameter(SysparamNames.getVersion()));
        reqeustContext.setAppKey(servletRequest.getParameter(SysparamNames.getAppKey()));
        reqeustContext.setSessionId(servletRequest.getParameter(SysparamNames.getSessionId()));
        reqeustContext.setLocale(getLocale(servletRequest));
        reqeustContext.setFormat(getFormat(servletRequest));
        reqeustContext.setMessageFormat(getResponseFormat(servletRequest));
        reqeustContext.setSign(servletRequest.getParameter(SysparamNames.getSign()));

        //设置服务处理器
        ServiceMethodHandler serviceMethodHandler =
                ropContext.getServiceMethodHandler(reqeustContext.getMethod(), reqeustContext.getVersion());
        reqeustContext.setServiceMethodHandler(serviceMethodHandler);

        //设置请求对象
        buildRopRequest(reqeustContext);
        return reqeustContext;
    }

    private String getFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SysparamNames.getFormat());
        if (messageFormat == null) {
            return MessageFormat.xml.name();
        } else {
            return messageFormat;
        }
    }

    public static Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(SysparamNames.getLocale()) != null) {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(webRequest.getParameter(SysparamNames.getLocale()));
            return (Locale) localeEditor.getValue();
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }

    public static MessageFormat getResponseFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SysparamNames.getFormat());
        if (MessageFormat.isValidFormat(messageFormat)) {
            return MessageFormat.getFormat(messageFormat);
        } else {
            return MessageFormat.xml;
        }
    }


    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link com.rop.RequestContext}的{@link com.rop.RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link com.rop.RequestContext}的属性列表中。
     *
     * @param requestContext
     */
    private void buildRopRequest(SimpleRequestContext requestContext) {
        AbstractRopRequest ropRequest = null;
        if (requestContext.getServiceMethodHandler().isRopRequestImplType()) {
            HttpServletRequest request =
                    (HttpServletRequest) requestContext.getRawRequestObject();
            BindingResult bindingResult = doBind(request, requestContext.getServiceMethodHandler().getRequestType());
            ropRequest = buildRopRequestFromBindingResult(requestContext, bindingResult);

            List<ObjectError> allErrors = bindingResult.getAllErrors();
                    requestContext.setAttribute(SimpleRequestContext.SPRING_VALIDATE_ERROR_ATTRNAME, allErrors);
        }else{
            ropRequest = new DefaultRopRequest();
        }
        ropRequest.setRequestContext(requestContext);
        requestContext.setRopRequest(ropRequest);
    }

    private AbstractRopRequest buildRopRequestFromBindingResult(RequestContext requestContext, BindingResult bindingResult) {
        AbstractRopRequest ropRequest = (AbstractRopRequest) bindingResult.getTarget();
        if (ropRequest instanceof AbstractRopRequest) {
            AbstractRopRequest abstractRopRequest = (AbstractRopRequest) ropRequest;
            abstractRopRequest.setRequestContext(requestContext);
        } else {
            logger.warn(ropRequest.getClass().getName() + "不是扩展于" + AbstractRopRequest.class.getName() +
                    ",无法设置" + RequestContext.class.getName());
        }
        return ropRequest;
    }

    private HashMap<String, String> getRequestParams(HttpServletRequest request) {
        Map srcParamMap = request.getParameterMap();
        HashMap<String, String> destParamMap = new HashMap<String, String>(srcParamMap.size());
        for (Object obj : srcParamMap.keySet()) {
            String[] values = (String[]) srcParamMap.get(obj);
            if (values != null && values.length > 0) {
                destParamMap.put((String) obj, values[0]);
            } else {
                destParamMap.put((String) obj, null);
            }
        }
        return destParamMap;
    }


    private BindingResult doBind(HttpServletRequest webRequest, Class<? extends RopRequest> requestType) {
        RopRequest bindObject = BeanUtils.instantiateClass(requestType);
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(bindObject, "bindObject");
        dataBinder.setConversionService(getConversionService());
        dataBinder.setValidator(getValidator());
        dataBinder.bind(webRequest);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }

    private Validator getValidator() {
        if (this.validator == null) {
            LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
            localValidatorFactoryBean.afterPropertiesSet();
            this.validator = localValidatorFactoryBean;
        }
        return this.validator;
    }

    public FormattingConversionService getConversionService() {
        return conversionService;
    }

    //默认的{@link RopRequest}实现类
    private class DefaultRopRequest extends AbstractRopRequest{
    }
}

