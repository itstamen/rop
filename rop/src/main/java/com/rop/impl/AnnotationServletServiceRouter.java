/**
 *
 * 日    期：12-2-8
 */
package com.rop.impl;

import com.rop.*;
import com.rop.MessageFormat;
import com.rop.config.SystemParameterNames;
import com.rop.event.*;
import com.rop.marshaller.JacksonJsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.marshaller.MessageMarshallerUtils;
import com.rop.request.RopRequestMessageConverter;
import com.rop.request.UploadFileConverter;
import com.rop.response.ErrorResponse;
import com.rop.response.RejectedServiceResponse;
import com.rop.response.ServiceUnavailableErrorResponse;
import com.rop.response.TimeoutErrorResponse;
import com.rop.security.*;
import com.rop.security.SecurityManager;
import com.rop.session.DefaultSessionManager;
import com.rop.session.SessionBindInterceptor;
import com.rop.session.SessionManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

public class AnnotationServletServiceRouter implements ServiceRouter {

    public static final String APPLICATION_XML = "application/xml";

    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String DEFAULT_EXT_ERROR_BASE_NAME = "i18n/rop/ropError";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String I18N_ROP_ERROR = "i18n/rop/error";

    private ServiceMethodAdapter serviceMethodAdapter = new AnnotationServiceMethodAdapter();

    private RopMarshaller xmlMarshallerRop = new JaxbXmlRopMarshaller();

    private RopMarshaller jsonMarshallerRop = new JacksonJsonRopMarshaller();

    private RequestContextBuilder requestContextBuilder;

    private SecurityManager securityManager;

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

    //会话管理器
    private SessionManager sessionManager = new DefaultSessionManager();

    //服务调用频率管理器
    private InvokeTimesController invokeTimesController = new DefaultInvokeTimesController();

    private Class<? extends ThreadFerry> threadFerryClass;

    private String extErrorBasename;

    private String[] extErrorBasenames;


    public void service(Object request, Object response) {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        //获取服务方法最大过期时间
        String method = servletRequest.getParameter(SystemParameterNames.getMethod());
        String version = servletRequest.getParameter(SystemParameterNames.getVersion());
        if (logger.isDebugEnabled()) {
            logger.debug("调用服务方法：" + method + "(" + version + ")");
        }
        int serviceMethodTimeout = getServiceMethodTimeout(method, version);
        long beginTime = System.currentTimeMillis();
        String jsonpCallback = getJsonpcallback(servletRequest);

        //使用异常方式调用服务方法
        try {

            //执行线程摆渡
            ThreadFerry threadFerry = buildThreadFerryInstance();
            if (threadFerry != null) {
                threadFerry.doInSrcThread();
            }

            ServiceRunnable runnable = new ServiceRunnable(servletRequest, servletResponse, jsonpCallback, threadFerry);
            Future<?> future = this.threadPoolExecutor.submit(runnable);
            while (!future.isDone()) {
                future.get(serviceMethodTimeout, TimeUnit.SECONDS);
            }
        } catch (RejectedExecutionException ree) {//超过最大的服务平台的最大资源限制，无法提供服务
            if (logger.isInfoEnabled()) {
                logger.info("调用服务方法:" + method + "(" + version + ")，超过最大资源限制，无法提供服务。");
            }
            RopRequestContext ropRequestContext = buildRequestContextWhenException(servletRequest, beginTime);
            RejectedServiceResponse ropResponse = new RejectedServiceResponse(ropRequestContext);
            writeResponse(ropResponse, servletResponse, ServletRequestContextBuilder.getResponseFormat(servletRequest), jsonpCallback);
            fireAfterDoServiceEvent(ropRequestContext);
        } catch (TimeoutException e) {//服务时间超限
            if (logger.isInfoEnabled()) {
                logger.info("调用服务方法:" + method + "(" + version + ")，服务调用超时。");
            }
            RopRequestContext ropRequestContext = buildRequestContextWhenException(servletRequest, beginTime);
            TimeoutErrorResponse ropResponse =
                    new TimeoutErrorResponse(ropRequestContext.getMethod(),
                            ropRequestContext.getLocale(), serviceMethodTimeout);
            writeResponse(ropResponse, servletResponse, ServletRequestContextBuilder.getResponseFormat(servletRequest), jsonpCallback);
            fireAfterDoServiceEvent(ropRequestContext);
        } catch (Throwable throwable) {//产生未知的错误
            if (logger.isInfoEnabled()) {
                logger.info("调用服务方法:" + method + "(" + version + ")，产生异常", throwable);
            }
            ServiceUnavailableErrorResponse ropResponse =
                    new ServiceUnavailableErrorResponse(method, ServletRequestContextBuilder.getLocale(servletRequest), throwable);
            writeResponse(ropResponse, servletResponse, ServletRequestContextBuilder.getResponseFormat(servletRequest), jsonpCallback);
            RopRequestContext ropRequestContext = buildRequestContextWhenException(servletRequest, beginTime);
            fireAfterDoServiceEvent(ropRequestContext);
        } finally {
            try {
                servletResponse.getOutputStream().flush();
                servletResponse.getOutputStream().close();
            } catch (IOException e) {
                logger.error("关闭响应出错", e);
            }
        }
    }

    /**
     * 获取JSONP的参数名，如果没有返回
     *
     * @param servletRequest
     * @return
     */
    private String getJsonpcallback(HttpServletRequest servletRequest) {
        if (servletRequest.getParameterMap().containsKey(SystemParameterNames.getJsonp())) {
            String callback = servletRequest.getParameter(SystemParameterNames.getJsonp());
            if (StringUtils.isEmpty(callback)) {
                callback = "callback";
            }
            return callback;
        } else {
            return null;
        }
    }


    public void startup() {
        if (logger.isInfoEnabled()) {
            logger.info("开始启动Rop框架...");
        }
        Assert.notNull(this.applicationContext, "Spring上下文不能为空");

        //初始化类型转换器
        if (this.formattingConversionService == null) {
            this.formattingConversionService = getDefaultConversionService();
        }
        registerConverters(formattingConversionService);

        //实例化ServletRequestContextBuilder
        this.requestContextBuilder = new ServletRequestContextBuilder(this.formattingConversionService);

        //设置校验器
        if (this.securityManager == null) {
            this.securityManager = new DefaultSecurityManager();
        }

        //设置异步执行器
        if (this.threadPoolExecutor == null) {
            this.threadPoolExecutor =
                    new ThreadPoolExecutor(200, Integer.MAX_VALUE, 5 * 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }

        //创建Rop上下文
        this.ropContext = buildRopContext();

        //初始化事件发布器
        this.ropEventMulticaster = buildRopEventMulticaster();

        //注册会话绑定拦截器
        this.addInterceptor(new SessionBindInterceptor());

        //初始化信息源
        initMessageSource();

        //产生Rop框架初始化事件
        fireAfterStartedRopEvent();

        if (logger.isInfoEnabled()) {
            logger.info("Rop框架启动成功！");
        }
    }

    private void registerConverters(FormattingConversionService conversionService) {
        conversionService.addConverter(new RopRequestMessageConverter());
        conversionService.addConverter(new UploadFileConverter());
    }

    private ThreadFerry buildThreadFerryInstance() {
        if (threadFerryClass != null) {
            return BeanUtils.instantiate(threadFerryClass);
        } else {
            return null;
        }
    }


    public void shutdown() {
        fireBeforeCloseRopEvent();
        threadPoolExecutor.shutdown();
    }


    public void setSignEnable(boolean signEnable) {
        if (!signEnable && logger.isWarnEnabled()) {
            logger.warn("rop close request message sign");
        }
        this.signEnable = signEnable;
    }


    public void setThreadFerryClass(Class<? extends ThreadFerry> threadFerryClass) {
        if (logger.isDebugEnabled()) {
            logger.debug("ThreadFerry set to {}",threadFerryClass.getName());
        }
        this.threadFerryClass = threadFerryClass;
    }


    public void setInvokeTimesController(InvokeTimesController invokeTimesController) {
        if (logger.isDebugEnabled()) {
            logger.debug("InvokeTimesController set to {}",invokeTimesController.getClass().getName());
        }
        this.invokeTimesController = invokeTimesController;
    }


    public void setServiceTimeoutSeconds(int serviceTimeoutSeconds) {
        if (logger.isDebugEnabled()) {
            logger.debug("serviceTimeoutSeconds set to {}",serviceTimeoutSeconds);
        }
        this.serviceTimeoutSeconds = serviceTimeoutSeconds;
    }


    public void setSecurityManager(SecurityManager securityManager) {
        if (logger.isDebugEnabled()) {
            logger.debug("securityManager set to {}",securityManager.getClass().getName());
        }
        this.securityManager = securityManager;
    }


    public void setFormattingConversionService(FormattingConversionService formatConversionService) {
        if (logger.isDebugEnabled()) {
            logger.debug("formatConversionService set to {}",formatConversionService.getClass().getName());
        }
        this.formattingConversionService = formatConversionService;
    }


    public void setSessionManager(SessionManager sessionManager) {
        if (logger.isDebugEnabled()) {
            logger.debug("sessionManager set to {}",sessionManager.getClass().getName());
        }
        this.sessionManager = sessionManager;
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


    public void setExtErrorBasename(String extErrorBasename) {
        if (logger.isDebugEnabled()) {
            logger.debug("extErrorBasename set to {}",extErrorBasename);
        }
        this.extErrorBasename = extErrorBasename;
    }


    public void setExtErrorBasenames(String[] extErrorBasenames) {
        if (extErrorBasenames != null) {
            List<String> list = new ArrayList<String>();
            for (String errorBasename : extErrorBasenames) {
                if (StringUtils.isNotBlank(errorBasename)) {
                    list.add(errorBasename);
                }
            }
            this.extErrorBasenames = list.toArray(new String[0]);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("extErrorBasenames set to {}",extErrorBasenames);
        }
    }


    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
        if (logger.isDebugEnabled()) {
            logger.debug("threadPoolExecutor set to {}",threadPoolExecutor.getClass().getName());
            logger.debug("corePoolSize:{}",threadPoolExecutor.getCorePoolSize());
            logger.debug("maxPoolSize:{}",threadPoolExecutor.getMaximumPoolSize());
            logger.debug("keepAliveSeconds:{} seconds",threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
            logger.debug("queueCapacity:{}",threadPoolExecutor.getQueue().remainingCapacity());
        }
    }


    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public RopContext getRopContext() {
        return this.ropContext;
    }


    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
        if (logger.isDebugEnabled()) {
            logger.debug("add  interceptor {}",interceptor.getClass().getName());
        }
    }


    public void addListener(RopEventListener listener) {
        this.listeners.add(listener);
        if (logger.isDebugEnabled()) {
            logger.debug("add  listener {}",listener.getClass().getName());
        }
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
            if (methodTimeout <= 0) {
                return getServiceTimeoutSeconds();
            } else {
                return methodTimeout;
            }
        }
    }

    private class ServiceRunnable implements Runnable {

        private HttpServletRequest servletRequest;
        private HttpServletResponse servletResponse;
        private ThreadFerry threadFerry;
        private String jsonpCallback;

        private ServiceRunnable(HttpServletRequest servletRequest,
                                HttpServletResponse servletResponse,
                                String jsonpCallback,
                                ThreadFerry threadFerry) {
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
            this.jsonpCallback = jsonpCallback;
            this.threadFerry = threadFerry;
        }


        public void run() {
            if (threadFerry != null) {
                threadFerry.doInDestThread();
            }

            RopRequestContext ropRequestContext = null;
            RopRequest ropRequest = null;
            try {
                //用系统级参数构造一个RequestContext实例（第一阶段绑定）
                ropRequestContext = requestContextBuilder.buildBySysParams(
                        ropContext, servletRequest, servletResponse);

                //验证系统级参数的合法性
                MainError mainError = securityManager.validateSystemParameters(ropRequestContext);
                if (mainError != null) {
                    ropRequestContext.setRopResponse(new ErrorResponse(mainError));
                } else {

                    //绑定业务数据（第二阶段绑定）
                    ropRequest = requestContextBuilder.buildRopRequest(ropRequestContext);

                    //进行其它检查业务数据合法性，业务安全等
                    mainError = securityManager.validateOther(ropRequestContext);
                    if (mainError != null) {
                        ropRequestContext.setRopResponse(new ErrorResponse(mainError));
                    } else {
                        firePreDoServiceEvent(ropRequestContext);

                        //服务处理前拦截
                        invokeBeforceServiceOfInterceptors(ropRequestContext);

                        if (ropRequestContext.getRopResponse() == null) { //拦截器未生成response
                            //如果拦截器没有产生ropResponse时才调用服务方法
                            ropRequestContext.setRopResponse(doService(ropRequest));

                            //输出响应前拦截
                            invokeBeforceResponseOfInterceptors(ropRequest);
                        }
                    }
                }
                //输出响应
                writeResponse(ropRequestContext.getRopResponse(), servletResponse, ropRequestContext.getMessageFormat(), jsonpCallback);
            } catch (Throwable e) {
                if (ropRequestContext != null) {
                    String method = ropRequestContext.getMethod();
                    Locale locale = ropRequestContext.getLocale();
                    if (logger.isDebugEnabled()) {
                        String message = java.text.MessageFormat.format("service {0} call error", method);
                        logger.debug(message,e);
                    }
                    ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale, e);

                    //输出响应前拦截
                    invokeBeforceResponseOfInterceptors(ropRequest);
                    writeResponse(ropResponse, servletResponse, ropRequestContext.getMessageFormat(), jsonpCallback);
                } else {
                    throw new RopException("RopRequestContext is null.", e);
                }
            } finally {
                if (ropRequestContext != null) {

                    //发布服务完成事件
                    ropRequestContext.setServiceEndTime(System.currentTimeMillis());

                    //完成一次服务请求，计算次数
                    invokeTimesController.caculateInvokeTimes(ropRequestContext.getAppKey(), ropRequestContext.getSession());
                    fireAfterDoServiceEvent(ropRequestContext);
                }
            }
        }
    }


    /**
     * 当发生异常时，创建一个请求上下文对象
     *
     * @param request
     * @param beginTime
     * @return
     */
    private RopRequestContext buildRequestContextWhenException(HttpServletRequest request, long beginTime) {
        RopRequestContext ropRequestContext = requestContextBuilder.buildBySysParams(ropContext, request, null);
        ropRequestContext.setServiceBeginTime(beginTime);
        ropRequestContext.setServiceEndTime(System.currentTimeMillis());
        return ropRequestContext;
    }

    private RopContext buildRopContext() {
        DefaultRopContext defaultRopContext = new DefaultRopContext(this.applicationContext);
        defaultRopContext.setSignEnable(this.signEnable);
        defaultRopContext.setSessionManager(sessionManager);
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

    private void fireAfterDoServiceEvent(RopRequestContext ropRequestContext) {
        this.ropEventMulticaster.multicastEvent(new AfterDoServiceEvent(this, ropRequestContext));
    }

    private void firePreDoServiceEvent(RopRequestContext ropRequestContext) {
        this.ropEventMulticaster.multicastEvent(new PreDoServiceEvent(this, ropRequestContext));
    }

    /**
     * 在服务调用之前拦截
     *
     * @param ropRequestContext
     */
    private void invokeBeforceServiceOfInterceptors(RopRequestContext ropRequestContext) {
        Interceptor tempInterceptor = null;
        try {
            if (interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {

                    interceptor.beforeService(ropRequestContext);

                    //如果有一个产生了响应，则阻止后续的调用
                    if (ropRequestContext.getRopResponse() != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("拦截器[" + interceptor.getClass().getName() + "]产生了一个RopResponse," +
                                    " 阻止本次服务请求继续，服务将直接返回。");
                        }
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            ropRequestContext.setRopResponse(new ServiceUnavailableErrorResponse(ropRequestContext.getMethod(), ropRequestContext.getLocale(), e));
            logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
        }
    }

    /**
     * 在服务调用之后，返回响应之前拦截
     *
     * @param ropRequest
     */
    private void invokeBeforceResponseOfInterceptors(RopRequest ropRequest) {
        RopRequestContext ropRequestContext = ropRequest.getRopRequestContext();
        Interceptor tempInterceptor = null;
        try {
            if (interceptors != null && interceptors.size() > 0) {
                for (Interceptor interceptor : interceptors) {
                    interceptor.beforeResponse(ropRequestContext);
                }
            }
        } catch (Throwable e) {
            ropRequestContext.setRopResponse(new ServiceUnavailableErrorResponse(ropRequestContext.getMethod(), ropRequestContext.getLocale(), e));
            logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
        }
    }

    private void writeResponse(Object ropResponse, HttpServletResponse httpServletResponse, MessageFormat messageFormat, String jsonpCallback) {
        try {
            if (!(ropResponse instanceof ErrorResponse) && messageFormat == MessageFormat.stream) {
                if (logger.isDebugEnabled()) {
                    logger.debug("使用{}输出方式，由服务自身负责响应输出工作.", MessageFormat.stream);
                }
                return;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("输出响应：" + MessageMarshallerUtils.getMessage(ropResponse, messageFormat));
            }
            RopMarshaller ropMarshaller = xmlMarshallerRop;
            String contentType = APPLICATION_XML;
            if (messageFormat == MessageFormat.json) {
                ropMarshaller = jsonMarshallerRop;
                contentType = APPLICATION_JSON;
            }
            httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "*");
            httpServletResponse.setCharacterEncoding(Constants.UTF8);
            httpServletResponse.setContentType(contentType);

            if (jsonpCallback != null) {
                httpServletResponse.getOutputStream().write(jsonpCallback.getBytes());
                httpServletResponse.getOutputStream().write('(');
            }
            ropMarshaller.marshaller(ropResponse, httpServletResponse.getOutputStream());
            if (jsonpCallback != null) {
                httpServletResponse.getOutputStream().write(')');
                httpServletResponse.getOutputStream().write(';');
            }
        } catch (IOException e) {
            throw new RopException(e);
        }
    }

    private Object doService(RopRequest ropRequest) {
        Object ropResponse = null;
        RopRequestContext context = ropRequest.getRopRequestContext();
        if (context.getMethod() == null) {
            ropResponse = new ErrorResponse(MainErrors.getError(
                    MainErrorType.MISSING_METHOD, context.getLocale(),
                    SystemParameterNames.getMethod()));
        } else if (!ropContext.isValidMethod(context.getMethod())) {
            MainError invalidMethodError = MainErrors.getError(
                    MainErrorType.INVALID_METHOD, context.getLocale(),context.getMethod());
            ropResponse = new ErrorResponse(invalidMethodError);
        } else {
            try {
                ropResponse = serviceMethodAdapter.invokeServiceMethod(ropRequest);
            } catch (Exception e) { //出错则招聘服务不可用的异常
                if (logger.isInfoEnabled()) {
                    logger.info("调用" + context.getMethod() + "时发生异常，异常信息为：" + e.getMessage());
                    e.printStackTrace();
                }
                ropResponse = new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale(), e);
            }
        }
        return ropResponse;
    }

    /**
     * 设置国际化资源信息
     */
    private void initMessageSource() {
        HashSet<String> baseNamesSet = new HashSet<String>();
        baseNamesSet.add(I18N_ROP_ERROR);//ROP自动的资源

        if (extErrorBasename == null && extErrorBasenames == null) {
            baseNamesSet.add(DEFAULT_EXT_ERROR_BASE_NAME);
        } else {
            if (extErrorBasename != null) {
                baseNamesSet.add(extErrorBasename);
            }
            if (extErrorBasenames != null) {
                baseNamesSet.addAll(Arrays.asList(extErrorBasenames));
            }
        }
        String[] totalBaseNames = baseNamesSet.toArray(new String[0]);

        if (logger.isInfoEnabled()) {
            logger.info("加载错误码国际化资源：{}", StringUtils.join(totalBaseNames, ","));
        }
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(totalBaseNames);
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
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