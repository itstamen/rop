/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.impl;

import com.rop.*;
import com.rop.config.SysparamNames;
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
 *    构建{@link ServiceMethodContext}实例
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServletRequestServiceMethodContextBuilder implements ServiceMethodContextBuilder {

    private FormattingConversionService conversionService;

    private Validator validator;


    @Override
    public SimpleServiceMethodContext buildBopServiceContext(RopContext ropContext, Object request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("请求对象必须是HttpServletRequest的类型");
        }

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        SimpleServiceMethodContext ropServiceContext = new SimpleServiceMethodContext(ropContext);

        //设置系统参数
        ropServiceContext.setMethod(servletRequest.getParameter(SysparamNames.getMethod()));
        ropServiceContext.setVersion(servletRequest.getParameter(SysparamNames.getVersion()));
        ropServiceContext.setAppKey(servletRequest.getParameter(SysparamNames.getAppKey()));
        ropServiceContext.setSessionId(servletRequest.getParameter(SysparamNames.getSessionId()));
        ropServiceContext.setLocale(getLocale(servletRequest));
        ropServiceContext.setFormat(getFormat(servletRequest));
        ropServiceContext.setMessageFormat(getResponseFormat(servletRequest));
        ropServiceContext.setSign(servletRequest.getParameter(SysparamNames.getSign()));

        //设置服务处理器
        ServiceMethodHandler serviceMethodHandler =
                ropContext.getServiceMethodHandler(ropServiceContext.getMethod(),ropServiceContext.getVersion());
        ropServiceContext.setServiceMethodHandler(serviceMethodHandler);

        //设置请求对象
        ropServiceContext.setAttribute(SimpleServiceMethodContext.HTTP_SERVLET_REQUEST_ATTRNAME, servletRequest);

        bindRequest(ropServiceContext);
        return ropServiceContext;
    }

    private String getFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SysparamNames.getFormat());
        if (messageFormat == null) {
            return MessageFormat.xml.name();
        } else {
            return messageFormat;
        }
    }

    private Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(SysparamNames.getLocale()) != null) {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(webRequest.getParameter(SysparamNames.getLocale()));
            return (Locale) localeEditor.getValue();
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }

    private MessageFormat getResponseFormat(HttpServletRequest webRequest) {
        String messageFormat = webRequest.getParameter(SysparamNames.getFormat());
        if (MessageFormat.isValidFormat(messageFormat)) {
            return MessageFormat.getFormat(messageFormat);
        } else {
            return MessageFormat.xml;
        }
    }


    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link com.rop.ServiceMethodContext}的{@link com.rop.RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link com.rop.ServiceMethodContext}的属性列表中。
     *
     * @param context
     */
    private void bindRequest(SimpleServiceMethodContext context) {
        HttpServletRequest request =
                (HttpServletRequest) context.getAttribute(SimpleServiceMethodContext.HTTP_SERVLET_REQUEST_ATTRNAME);

        //按参数名和属性名称匹配绑定数据
        BindingResult bindingResult = doBind(request, context.getServiceMethodHandler().getRequestType());

        //1.创建RopRequest
        RopRequest ropRequest = buildRopRequest(context, request, bindingResult);
        context.setRopRequest(ropRequest);

        //2.设置校验错误信息
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        context.setAttribute(SimpleServiceMethodContext.SPRING_VALIDATE_ERROR_ATTRNAME, allErrors);
    }

    private RopRequest buildRopRequest(ServiceMethodContext context, HttpServletRequest request, BindingResult bindingResult) {
        RopRequest ropRequest = (RopRequest) bindingResult.getTarget();
        ropRequest.setRawRequestObject(request);

        //1.设置系统参数
        ropRequest.setAppKey(context.getAppKey());
        ropRequest.setSessionId(context.getSessionId());
        ropRequest.setMethod(context.getMethod());
        ropRequest.setVersion(context.getVersion());
        ropRequest.setMsgFormat(context.getMessageFormat());
        ropRequest.setLocale(context.getLocale());
        ropRequest.setSign(context.getSign());

        //2.将HttpServletRequest的所有参数填充到ropRequest中
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
        ropRequest.setParamValues(destParamMap);

        return ropRequest;
    }


    private BindingResult doBind(HttpServletRequest webRequest, Class<? extends RopRequest> requestType) {
        RopRequest bindObject = BeanUtils.instantiateClass(requestType);
        bindObject.setIp(webRequest.getRemoteAddr());
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
            Set<Object> converters = new HashSet<Object>();
            converters.add(new RopRequestMessageConverter());
            serviceFactoryBean.setConverters(converters);
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

