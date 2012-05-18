/**
 *
 * 日    期：12-2-8
 */
package com.rop.impl;

import com.rop.*;
import com.rop.marshaller.JacksonJsonRopResponseMarshaller;
import com.rop.marshaller.JaxbXmlRopResponseMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.response.ServiceUnavailableErrorResponse;
import com.rop.validation.MainErrorType;
import com.rop.validation.MainErrors;
import com.rop.validation.SubErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class AnnotationRopServiceRouter implements RopServiceRouter {

    //方法的参数名
    private static final String METHOD = "method";

    //格式化参数名
    public static final String FORMAT = "format";

    public static final String MSG_FORMAT = "msgFormat";

    //本地化参数名
    private static final String LOCALE = "locale";

    public static final String SESSION_ID = "sessionId";

    public static final String APP_KEY = "appKey";
    public static final String UTF_8 = "UTF-8";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_JSON = "application/json";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String I18N_ROP_ERROR = "i18n/rop/error";

    private RopConfig ropConfig;

    private RopServiceMethodAdapter ropServiceMethodAdapter;

    private RopResponseMarshaller xmlMarshallerRop = new JaxbXmlRopResponseMarshaller();

    private RopResponseMarshaller jsonMarshallerRop = new JacksonJsonRopResponseMarshaller();

    private RopServiceHandlerRegistry ropServiceHandlerRegistry;

    private boolean isInitialized = false;

    private FormattingConversionService conversionService;

    private Validator validator;

    private List<Interceptor> interceptors;

    public AnnotationRopServiceRouter(RopConfig ropConfig) {
        this.ropConfig = ropConfig;
        this.interceptors = ropConfig.getInterceptors();
    }

    public void service(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        try {
            initializeOnlyOnce();

            //绑定数据
            SimpleRopServiceContext context = buildBopServiceContext(request);

            //服务处理前拦截
            invokeBeforceServiceOfInterceptors(context);

            //如果拦截器没有产生ropResponse时才调用服务方法
            if (context.getRopResponse() == null) {
                context.setRopResponse(doService(context));
                //返回响应前拦截
                invokeBeforceResponseOfInterceptors(context);
            }

            //输出响应
            writeResponse(context.getRopResponse(), httpServletResponse, context.getResponseFormat());

        } catch (Throwable e) {
            String method = request.getParameter(METHOD);
            Locale locale = getLocale(request);
            ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale);
            writeResponse(ropResponse, httpServletResponse, getResponseFormat(request));
        }
    }

    /**
     * 在服务调用之前拦截
     *
     * @param context
     */
    private void invokeBeforceServiceOfInterceptors(SimpleRopServiceContext context) {
        Interceptor tempInterceptor = null;
        try {
            if (this.interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {

                    interceptor.beforeService(context);

                    //如果有一个产生了响应，则阻止后续的调用
                    if (context.getRopResponse() != null) {
                        if(logger.isDebugEnabled()){
                            logger.debug("拦截器["+interceptor.getClass().getName()+ "]产生了一个RopResponse,"+
                              " 阻止本次服务请求继续，服务将直接返回。");
                        }
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            context.setRopResponse(new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale()));
            logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
        }
    }

    /**
     * 在服务调用之后，返回响应之前拦截
     *
     * @param context
     */
    private void invokeBeforceResponseOfInterceptors(SimpleRopServiceContext context) {
        Interceptor tempInterceptor = null;
        try {
            if (this.interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {
                    interceptor.beforeResponse(context);
                }
            }
        } catch (Throwable e) {
            context.setRopResponse(new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale()));
            logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
        }
    }

    private SimpleRopServiceContext buildBopServiceContext(HttpServletRequest request) {
        SimpleRopServiceContext ropServiceContext = new SimpleRopServiceContext();
        ropServiceContext.setMethod(request.getParameter(METHOD));
        ropServiceContext.setLocale(getLocale(request));
        ropServiceContext.setRopServiceHandler(ropServiceHandlerRegistry.getBopServiceHandler(request.getParameter(METHOD)));
        ropServiceContext.setResponseFormat(getResponseFormat(request));
        ropServiceContext.setAttribute(SimpleRopServiceContext.HTTP_SERVLET_REQUEST_ATTRNAME, request);
        ropServiceContext.setSessionId(request.getParameter(SESSION_ID));
        ropServiceContext.setAppKey(request.getParameter(APP_KEY));
        ropServiceContext.setNeedCheckSign(ropConfig.isNeedCheckSign());
        bindRequest(ropServiceContext);
        return ropServiceContext;
    }


    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link RopServiceContext}的{@link RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link RopServiceContext}的属性列表中。
     *
     * @param context
     */
    private void bindRequest(RopServiceContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getAttribute(SimpleRopServiceContext.HTTP_SERVLET_REQUEST_ATTRNAME);
        BindingResult bindingResult = doBind(request, context.getRopServiceHandler().getRequestType());
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
        context.setAttribute(SimpleRopServiceContext.SPRING_VALIDATE_ERROR_ATTRNAME, allErrors);
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

    private ResponseFormat getResponseFormat(HttpServletRequest webRequest) {
        String formatValue = webRequest.getParameter(MSG_FORMAT);
        if (formatValue == null) {
            formatValue = webRequest.getParameter(FORMAT);
        }
        return ResponseFormat.getFormat(formatValue);
    }

    private void writeResponse(RopResponse ropResponse, HttpServletResponse httpServletResponse, ResponseFormat responseFormat) {
        try {
            httpServletResponse.setCharacterEncoding(UTF_8);
            if (responseFormat == ResponseFormat.XML) {
                httpServletResponse.setContentType(APPLICATION_XML);
                xmlMarshallerRop.marshaller(ropResponse, httpServletResponse.getOutputStream());
            } else {
                httpServletResponse.setContentType(APPLICATION_JSON);
                jsonMarshallerRop.marshaller(ropResponse, httpServletResponse.getOutputStream());
            }
        } catch (IOException e) {
            throw new RopException(e);
        }
    }

    private RopResponse doService(RopServiceContext context) {
        RopResponse ropResponse = null;
        if (context.getMethod() == null) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.MISSING_METHOD, context.getLocale()));
        } else if (!ropServiceHandlerRegistry.isValidMethod(context.getMethod())) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.INVALID_METHOD, context.getLocale()));
        } else {
            try {
                ropResponse = ropServiceMethodAdapter.invokeServiceMethod(context);
            } catch (Exception e) { //出错则招聘服务不可用的异常
                if (logger.isInfoEnabled()) {
                    logger.info("调用" + context.getMethod() + "时发生异常，异常信息为：" + e.getMessage());
                }
                ropResponse = new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale());
            }
        }
        return ropResponse;
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

    /**
     * 在WebApplication初始化完成后执行，完成BOP的初始化工作
     *
     * @param event
     */
    public void initializeOnlyOnce() {
        if (logger.isInfoEnabled()) {
            logger.info("扫描并注册ROP的服务方法");
        }
        if (!isInitialized) {
            this.ropServiceHandlerRegistry = new DefaultRopServiceMethodHandlerRegistry(ropConfig.getApplicationContext());
            this.ropServiceMethodAdapter = new AnnotationRopServiceMethodAdapter(this.ropConfig);
            initMessageSource();
            isInitialized = true;
        }
    }

    /**
     * 设置国际化资源信息
     */
    private void initMessageSource() {
        if (logger.isDebugEnabled()) {
            logger.debug("注册错误码国际化资源：" + I18N_ROP_ERROR + "," + ropConfig.getErrorResourceBaseName());
        }
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR, ropConfig.getErrorResourceBaseName());
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
    }
}

