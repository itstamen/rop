/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.impl;

import com.rop.MessageFormat;
import com.rop.RopContext;
import com.rop.RopRequest;
import com.rop.ServiceMethodContextBuilder;
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

    //方法的参数名
    private static final String METHOD = "method";

    //格式化参数名
    public static final String FORMAT = "format";

    //format的同义词
    public static final String MSG_FORMAT = "msgFormat";

    //本地化参数名
    private static final String LOCALE = "locale";

    public static final String SESSION_ID = "sessionId";

    public static final String APP_KEY = "appKey";

    private FormattingConversionService conversionService;

    private Validator validator;



    @Override
    public SimpleServiceMethodContext buildBopServiceContext(RopContext ropContext,Object request) {
        if (!(request instanceof HttpServletRequest)) {
            throw new IllegalArgumentException("请求对象必须是HttpServletRequest的类型");
        }

        HttpServletRequest servletRequest = (HttpServletRequest) request;

        SimpleServiceMethodContext ropServiceContext = new SimpleServiceMethodContext(ropContext);

        ropServiceContext.setMethod(servletRequest.getParameter(METHOD));
        ropServiceContext.setLocale(getLocale(servletRequest));
        ropServiceContext.setServiceMethodHandler(ropContext.getServiceMethodHandler(servletRequest.getParameter(METHOD)));
        ropServiceContext.setMessageFormat(getResponseFormat(servletRequest));
        ropServiceContext.setAttribute(SimpleServiceMethodContext.HTTP_SERVLET_REQUEST_ATTRNAME, servletRequest);
        ropServiceContext.setSessionId(servletRequest.getParameter(SESSION_ID));
        ropServiceContext.setAppKey(servletRequest.getParameter(APP_KEY));
        bindRequest(ropServiceContext);
        return ropServiceContext;
    }

    private Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(LOCALE) != null) {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(webRequest.getParameter(LOCALE));
            return (Locale) localeEditor.getValue();
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }

    private MessageFormat getResponseFormat(HttpServletRequest webRequest) {
        String formatValue = webRequest.getParameter(MSG_FORMAT);
        if (formatValue == null) {
            formatValue = webRequest.getParameter(FORMAT);
        }
        return MessageFormat.getFormat(formatValue);
    }


    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link com.rop.ServiceMethodContext}的{@link com.rop.RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link com.rop.ServiceMethodContext}的属性列表中。
     *
     * @param context
     */
    private void bindRequest(SimpleServiceMethodContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getAttribute(SimpleServiceMethodContext.HTTP_SERVLET_REQUEST_ATTRNAME);
        BindingResult bindingResult = doBind(request, context.getServiceMethodHandler().getRequestType());
        RopRequest ropRequest = (RopRequest) bindingResult.getTarget();
        ropRequest.setRawRequestObject(request);

        //将HttpServletRequest的所有参数填充到ropRequest中
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

        //1.设置ropRequest
        context.setRopRequest(ropRequest);

        //2.设置校验错误信息
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        context.setAttribute(SimpleServiceMethodContext.SPRING_VALIDATE_ERROR_ATTRNAME, allErrors);
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

