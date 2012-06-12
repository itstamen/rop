/**
 *
 * 日    期：12-2-8
 */
package com.rop.impl;

import com.rop.*;
import com.rop.config.SystemParameterNames;
import com.rop.event.*;
import com.rop.marshaller.JacksonJsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.response.ServiceUnavailableErrorResponse;
import com.rop.response.TimeoutErrorResponse;
import com.rop.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

public class AnnotationServletServiceRouter implements ServiceRouter {

    public static final String UTF_8 = "UTF-8";

    public static final String APPLICATION_XML = "application/xml";

    public static final String APPLICATION_JSON = "application/json";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String I18N_ROP_ERROR = "i18n/rop/error";

    private ServiceMethodAdapter serviceMethodAdapter = new AnnotationServiceMethodAdapter();

    private RopMarshaller xmlMarshallerRop = new JaxbXmlRopMarshaller();

    private RopMarshaller jsonMarshallerRop = new JacksonJsonRopMarshaller();

    private RequestContextBuilder requestContextBuilder;

    private RopValidator ropValidator;

    private FormattingConversionService formattingConversionService;

    private ThreadPoolExecutor threadPoolExecutor;

    private RopContext ropContext;

    private RopEventMulticaster ropEventMulticaster;

    private List<Interceptor> interceptors = new ArrayList<Interceptor>();

    private List<RopEventListener> listeners = new ArrayList<RopEventListener>();

    private boolean signEnable = true;

    private ApplicationContext applicationContext;

    //所有服务方法的最大过期时间，单位为秒(0或负数代表不限制)
    private int serviceTimeoutSeconds = Integer.MAX_VALUE;

    private String extErrorBasename = "i18n/rop/ropError";

    @Override
    public void service(Object request, Object response) {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        //获取服务方法最大过期时间
        String method = servletRequest.getParameter(SystemParameterNames.getMethod());
        String version = servletRequest.getParameter(SystemParameterNames.getVersion());
        int serviceMethodTimeout = getServiceMethodTimeout(method, version);

        //使用异常方式调用服务方法
        try {
            ServiceRunnable runnable = new ServiceRunnable(servletRequest, servletResponse);
            Future<?> future = this.threadPoolExecutor.submit(runnable);
            while (!future.isDone()) {
                future.get(serviceMethodTimeout, TimeUnit.SECONDS);
            }
        } catch (TimeoutException e) {
            TimeoutErrorResponse ropResponse =
                    new TimeoutErrorResponse(ServletRequestContextBuilder.getLocale(servletRequest));
            writeResponse(ropResponse, servletResponse, ServletRequestContextBuilder.getResponseFormat(servletRequest));
        } catch (Throwable throwable) {
            ServiceUnavailableErrorResponse ropResponse =
                    new ServiceUnavailableErrorResponse(method, ServletRequestContextBuilder.getLocale(servletRequest));
            writeResponse(ropResponse, servletResponse, ServletRequestContextBuilder.getResponseFormat(servletRequest));
        }
    }

    @Override
    public void startup() {
        if (logger.isInfoEnabled()) {
            logger.info("开始启动Rop框架...");
        }
        Assert.notNull(this.applicationContext, "Spring上下文不能为空");

        //初始化类型转换器
        if (this.formattingConversionService == null) {
            this.formattingConversionService = getDefaultConversionService();
        }
        this.formattingConversionService.addConverter(new RopRequestMessageConverter());//支持JAXB的转换器

        //实例化ServletRequestContextBuilder
        this.requestContextBuilder = new ServletRequestContextBuilder(this.formattingConversionService);

        //设置校验器
        if (this.ropValidator == null) {
            this.ropValidator = new DefaultRopValidator();
        }

        //设置异步执行器
        if (this.threadPoolExecutor == null) {
            this.threadPoolExecutor =
                    new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        }

        //创建Rop上下文
        this.ropContext = buildRopContext();

        //初始化事件发布器
        this.ropEventMulticaster = buildRopEventMulticaster();

        //初始化信息源
        initMessageSource();

        //产生Rop框架初始化事件
        fireAfterStartedRopEvent();

        if (logger.isInfoEnabled()) {
            logger.info("Rop框架启动成功！");
        }
    }

    @Override
    public void shutdown() {
        fireBeforeCloseRopEvent();
        threadPoolExecutor.shutdown();
    }

    @Override
    public void setSignEnable(boolean signEnable) {
        if (!signEnable && logger.isInfoEnabled()) {
            logger.info("关闭签名验证功能");
        }
        this.signEnable = signEnable;
    }

    @Override
    public void setServiceTimeoutSeconds(int serviceTimeoutSeconds) {
        this.serviceTimeoutSeconds = serviceTimeoutSeconds;
    }

    @Override
    public void setRopValidator(RopValidator ropValidator) {
        this.ropValidator = ropValidator;
    }

    @Override
    public void setFormattingConversionService(FormattingConversionService formatConversionService) {
        this.formattingConversionService = formatConversionService;
    }

    /**
     * 获取默认的格式化转换器
     *
     * @return
     */
    private FormattingConversionService getDefaultConversionService() {
        FormattingConversionServiceFactoryBean serviceFactoryBean = new FormattingConversionServiceFactoryBean();
        serviceFactoryBean.afterPropertiesSet();
        return serviceFactoryBean.getObject();
    }

    @Override
    public void setExtErrorBasename(String extErrorBasename) {
        this.extErrorBasename = extErrorBasename;
    }

    @Override
    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public RopContext getRopContext() {
        return this.ropContext;
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    @Override
    public void addListener(RopEventListener listener) {
        this.listeners.add(listener);
    }

    public int getServiceTimeoutSeconds() {
        return serviceTimeoutSeconds > 0 ? serviceTimeoutSeconds : Integer.MAX_VALUE;
    }

    /**
     * 取最小的过期时间
     *
     * @param method
     * @param version
     * @return
     */
    private int getServiceMethodTimeout(String method, String version) {
        ServiceMethodHandler serviceMethodHandler = ropContext.getServiceMethodHandler(method, version);
        if (serviceMethodHandler == null) {
            return getServiceTimeoutSeconds();
        } else {
            int methodTimeout = serviceMethodHandler.getServiceMethodDefinition().getTimeout();
            int serviceTimeout = getServiceTimeoutSeconds();
            return serviceTimeout > methodTimeout ? methodTimeout : serviceTimeout;
        }
    }

    private class ServiceRunnable implements Runnable {

        private HttpServletRequest servletRequest;
        private HttpServletResponse servletResponse;

        private ServiceRunnable(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }

        @Override
        public void run() {
            long beginTime = System.currentTimeMillis();
            RequestContext requestContext = null;

            try {
                //用系统级参数构造一个RequestContext实例（第一阶段绑定）
                requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest);

                //验证系统级参数的友好性
                MainError mainError = ropValidator.validateSysparams(requestContext);
                if (mainError != null) {
                    requestContext.setRopResponse(new ErrorResponse(mainError));
                } else {

                    //绑定业务数据（第二阶段绑定）
                    requestContextBuilder.bindBusinessParams(requestContext);

                    //进行其它检查业务数据合法性，业务安全等
                    mainError = ropValidator.validateOther(requestContext);
                    if (mainError != null) {
                        requestContext.setRopResponse(new ErrorResponse(mainError));
                    } else {
                        firePreDoServiceEvent(requestContext);

                        //服务处理前拦截
                        invokeBeforceServiceOfInterceptors(requestContext);

                        if (requestContext.getRopResponse() == null) { //拦截器未生成response
                            //如果拦截器没有产生ropResponse时才调用服务方法
                            requestContext.setRopResponse(doService(requestContext));

                            //返回响应后拦截
                            invokeBeforceResponseOfInterceptors(requestContext);
                        }
                    }
                }
                //输出响应
                writeResponse(requestContext.getRopResponse(), servletResponse, requestContext.getMessageFormat());
            } catch (Throwable e) {
                String method = requestContext.getMethod();
                Locale locale = requestContext.getLocale();
                ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale);
                writeResponse(ropResponse, servletResponse, requestContext.getMessageFormat());
            } finally {
                if (requestContext != null) {
                    //发布服务完成事件
                    requestContext.setServiceEndTime(System.currentTimeMillis());
                    fireAfterDoServiceEvent(requestContext);
                }
            }
        }
    }

    private RopContext buildRopContext() {
        DefaultRopContext defaultRopContext = new DefaultRopContext(this.applicationContext);
        defaultRopContext.setSignEnable(this.signEnable);
        return defaultRopContext;
    }

    private RopEventMulticaster buildRopEventMulticaster() {

        SimpleRopEventMulticaster simpleRopEventMulticaster = new SimpleRopEventMulticaster();

        //设置异步执行器
        if (this.threadPoolExecutor != null) {
            simpleRopEventMulticaster.setExecutor(this.threadPoolExecutor);
        }

        //添加事件监听器
        if (this.listeners != null && this.listeners.size() > 0) {
            for (RopEventListener ropEventListener : this.listeners) {
                simpleRopEventMulticaster.addRopListener(ropEventListener);
            }
        }

        return simpleRopEventMulticaster;
    }

    /**
     * 发布Rop启动后事件
     */
    private void fireAfterStartedRopEvent() {
        AfterStartedRopEvent ropEvent = new AfterStartedRopEvent(this, this.ropContext);
        this.ropEventMulticaster.multicastEvent(ropEvent);
    }

    /**
     * 发布Rop启动后事件
     */
    private void fireBeforeCloseRopEvent() {
        PreCloseRopEvent ropEvent = new PreCloseRopEvent(this, this.ropContext);
        this.ropEventMulticaster.multicastEvent(ropEvent);
    }

    private void fireAfterDoServiceEvent(RequestContext requestContext) {
        this.ropEventMulticaster.multicastEvent(new PreDoServiceEvent(this, requestContext));
    }

    private void firePreDoServiceEvent(RequestContext requestContext) {
        this.ropEventMulticaster.multicastEvent(new AfterDoServiceEvent(this, requestContext));
    }

    /**
     * 在服务调用之前拦截
     *
     * @param context
     */
    private void invokeBeforceServiceOfInterceptors(RequestContext context) {
        Interceptor tempInterceptor = null;
        try {
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
    private void invokeBeforceResponseOfInterceptors(RequestContext context) {
        Interceptor tempInterceptor = null;
        try {
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

    private RopResponse doService(RequestContext methodContext) {
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
        if (logger.isInfoEnabled()) {
            logger.info("加载错误码国际化资源：" + I18N_ROP_ERROR + "," + this.extErrorBasename);
        }
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR, this.extErrorBasename);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
    }

    public RopValidator getRopValidator() {
        return ropValidator;
    }

    public FormattingConversionService getFormattingConversionService() {
        return formattingConversionService;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public RopEventMulticaster getRopEventMulticaster() {
        return ropEventMulticaster;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public List<RopEventListener> getListeners() {
        return listeners;
    }

    public boolean isSignEnable() {
        return signEnable;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public String getExtErrorBasename() {
        return extErrorBasename;
    }
}