/**
 *
 * 日    期：12-2-8
 */
package com.rop.impl;

import com.rop.*;
import com.rop.event.*;
import com.rop.marshaller.JacksonJsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.response.ServiceUnavailableErrorResponse;
import com.rop.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ServletServiceRouter implements ServiceRouter {


    public static final String UTF_8 = "UTF-8";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_JSON = "application/json";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String I18N_ROP_ERROR = "i18n/rop/error";

    private ServiceMethodAdapter serviceMethodAdapter;

    private RopMarshaller xmlMarshallerRop = new JaxbXmlRopMarshaller();

    private RopMarshaller jsonMarshallerRop = new JacksonJsonRopMarshaller();

    private ServiceMethodContextBuilder serviceMethodContextBuilder = new ServletRequestServiceMethodContextBuilder();

    private RopContext ropContext;

    private RopValidator ropValidator;

    private RopEventMulticaster ropEventMulticaster;


    public ServletServiceRouter(RopConfig ropConfig) {
        if (logger.isInfoEnabled()) {
            logger.info("初始化Rop框架");
        }
        this.ropContext = new DefaultRopContext(ropConfig);
        this.serviceMethodAdapter = new AnnotationServiceMethodAdapter();
        this.ropValidator = new DefaultRopValidator(this.ropContext.getRopConfig());

        //初始化信息源
        initMessageSource();

        //初始化事件发布器
        initRopEventMulticaster();

        //产生Rop框架初始化事件
        firePostInitializeEvent();
    }

    private void initRopEventMulticaster() {
        SimpleRopEventMulticaster simpleRopEventMulticaster = new SimpleRopEventMulticaster();

        //设置异步执行器
        if (ropContext.getRopConfig().getTaskExecutor() != null) {
            simpleRopEventMulticaster.setTaskExecutor(ropContext.getRopConfig().getTaskExecutor());
        }

        //添加事件监听器
        List<RopEventListener> ropEventListeners = this.ropContext.getRopConfig().getRopEventListeners();
        if (ropEventListeners != null && ropEventListeners.size() > 0) {
            for (RopEventListener ropEventListener : ropEventListeners) {
                simpleRopEventMulticaster.addRopListener(ropEventListener);
            }
        }

        this.ropEventMulticaster = simpleRopEventMulticaster;
    }

    /**
     * 发布Rop初始化事件
     */
    private void firePostInitializeEvent() {
        PostInitializeEvent event = new PostInitializeEvent(this, this.ropContext);
        this.ropEventMulticaster.multicastEvent(event);
    }

    @Override
    public void service(Object request, Object response) {
        long beginTime = System.currentTimeMillis();
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        ServiceMethodContext serviceMethodContext = null;

        try {
            //构造服务方法的上下文
            serviceMethodContext = buildBopServiceContext(servletRequest);
            serviceMethodContext.setServiceBeginTime(beginTime);

            //发布事件
            firePreDoServiceEvent(serviceMethodContext);

            //进行服务准入检查（包括appKey、签名、会话、服务安全，数据合法性等）
            MainError mainError = ropValidator.validate(serviceMethodContext);

            if (mainError != null) {
                serviceMethodContext.setRopResponse(new ErrorResponse(mainError));
            } else {//通过服务准入检查后发起正式的服务调整

                //服务处理前拦截
                invokeBeforceServiceOfInterceptors(serviceMethodContext);

                //如果拦截器没有产生ropResponse时才调用服务方法
                serviceMethodContext.setRopResponse(doService(serviceMethodContext));

                //返回响应后拦截
                invokeBeforceResponseOfInterceptors(serviceMethodContext);
            }


            //输出响应
            writeResponse(serviceMethodContext.getRopResponse(), servletResponse, serviceMethodContext.getMessageFormat());
        } catch (Throwable e) {
            String method = serviceMethodContext.getMethod();
            Locale locale = serviceMethodContext.getLocale();
            ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale);
            writeResponse(ropResponse, servletResponse, serviceMethodContext.getMessageFormat());
        } finally {
            if (serviceMethodContext != null) {
                //发布服务完成事件
                serviceMethodContext.setServiceEndTime(System.currentTimeMillis());
                fireAfterDoServiceEvent(serviceMethodContext);
            }
        }
    }

    private void fireAfterDoServiceEvent(ServiceMethodContext serviceMethodContext) {
        this.ropEventMulticaster.multicastEvent(new PreDoServiceEvent(this, serviceMethodContext));
    }

    private void firePreDoServiceEvent(ServiceMethodContext serviceMethodContext) {
        this.ropEventMulticaster.multicastEvent(new AfterDoServiceEvent(this, serviceMethodContext));
    }


    /**
     * 在服务调用之前拦截
     *
     * @param context
     */
    private void invokeBeforceServiceOfInterceptors(ServiceMethodContext context) {
        Interceptor tempInterceptor = null;
        try {
            List<Interceptor> interceptors = ropContext.getRopConfig().getInterceptors();
            if (interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {

                    interceptor.beforeService(context);

                    //如果有一个产生了响应，则阻止后续的调用
                    if (context.getRopResponse() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("拦截器[" + interceptor.getClass().getName() + "]产生了一个RopResponse," +
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
    private void invokeBeforceResponseOfInterceptors(ServiceMethodContext context) {
        Interceptor tempInterceptor = null;
        try {
            List<Interceptor> interceptors = ropContext.getRopConfig().getInterceptors();
            if (interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {
                    interceptor.beforeResponse(context);
                }
            }
        } catch (Throwable e) {
            context.setRopResponse(new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale()));
            logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
        }
    }

    private ServiceMethodContext buildBopServiceContext(HttpServletRequest request) {
        return serviceMethodContextBuilder.buildBopServiceContext(this.ropContext, request);
    }


    private void writeResponse(RopResponse ropResponse, HttpServletResponse httpServletResponse, MessageFormat messageFormat) {
        try {
            httpServletResponse.setCharacterEncoding(UTF_8);
            if (messageFormat == MessageFormat.xml) {
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

    private RopResponse doService(ServiceMethodContext methodContext) {
        RopResponse ropResponse = null;
        if (methodContext.getMethod() == null) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.MISSING_METHOD, methodContext.getLocale()));
        } else if (!ropContext.isValidMethod(methodContext.getMethod())) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.INVALID_METHOD, methodContext.getLocale()));
        } else {
            try {
                ropResponse = serviceMethodAdapter.invokeServiceMethod(methodContext);
            } catch (Exception e) { //出错则招聘服务不可用的异常
                if (logger.isInfoEnabled()) {
                    logger.info("调用" + methodContext.getMethod() + "时发生异常，异常信息为：" + e.getMessage());
                }
                ropResponse = new ServiceUnavailableErrorResponse(methodContext.getMethod(), methodContext.getLocale());
            }
        }
        return ropResponse;
    }

    /**
     * 设置国际化资源信息
     */
    private void initMessageSource() {
        if (logger.isDebugEnabled()) {
            logger.debug("注册错误码国际化资源：" + I18N_ROP_ERROR + "," + ropContext.getRopConfig().getErrorResourceBaseName());
        }
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR, ropContext.getRopConfig().getErrorResourceBaseName());
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
    }
}

