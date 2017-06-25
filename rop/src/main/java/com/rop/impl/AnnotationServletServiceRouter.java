/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rop.impl;

import com.rop.*;
import com.rop.config.SystemParameterNames;
import com.rop.event.*;
import com.rop.marshaller.JacksonJsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.request.RopRequestMessageConverter;
import com.rop.request.UploadFileConverter;
import com.rop.response.ErrorResponse;
import com.rop.response.MainError;
import com.rop.response.MainErrorType;
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

	private ServiceMethodAdapter serviceMethodAdapter;

	private RopMarshaller xmlMarshaller;

	private RopMarshaller jsonMarshaller;

	private RequestContextBuilder requestContextBuilder;

	private SecurityManager securityManager;

	private FormattingConversionService formattingConversionService;

	private ThreadPoolExecutor threadPoolExecutor;

	private RopContext ropContext;

	private RopEventMulticaster ropEventMulticaster;

	private List<Interceptor> interceptors = new ArrayList<Interceptor>();

	private List<RopEventListener<RopEvent>> listeners = new ArrayList<RopEventListener<RopEvent>>();

	private boolean signEnable = true;

	private ApplicationContext applicationContext;

	// 所有服务方法的最大过期时间，单位为秒(0或负数代表不限制)
	private int serviceTimeoutSeconds = Integer.MAX_VALUE;

	// 会话管理器
	private SessionManager sessionManager;

	// 服务调用频率管理器
	private InvokeTimesController invokeTimesController;

	// 线程摆渡类，用于线程变量过渡设置
	private Class<? extends ThreadFerry> threadFerryClass;

	// 扩展的错误代码国际化资源开始名称
	private String extErrorBasename;
	// 扩展的错误代码国际化资源开始名称数组
	private String[] extErrorBasenames;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		// 获取服务方法最大过期时间
		String method = request.getParameter(SystemParameterNames.getMethod());
		String version = request.getParameter(SystemParameterNames.getVersion());
		if (logger.isDebugEnabled()) {
			logger.debug("调用服务方法：" + method + "(" + version + ")");
		}
		int serviceMethodTimeout = getServiceMethodTimeout(method, version);
		long beginTime = System.currentTimeMillis();
		String jsonpCallback = getJsonpcallback(request);
		MessageFormat format = ServletRequestContextBuilder.getResponseFormat(request);
		Locale locale = ServletRequestContextBuilder.getLocale(request);
		// 使用异常方式调用服务方法
		try {
			// 执行线程摆渡
			ThreadFerry threadFerry = buildThreadFerryInstance();
			if (threadFerry != null) {
				threadFerry.doInSrcThread();
			}
			ServiceRunnable runnable = new ServiceRunnable(request, response, threadFerry);
			Future<?> future = this.threadPoolExecutor.submit(runnable);
			while (!future.isDone()) {
				future.get(serviceMethodTimeout, TimeUnit.SECONDS);
			}
			// 为了解决子线程在输出内容时超时，将正确执行的结果和超时异常信息同时输出给客户端的bug
			if (runnable.ropRequestContext != null && runnable.ropRequestContext.getRopResponse() != null) {
				writeResponse(runnable.ropRequestContext.getRopResponse(), response, format, jsonpCallback);
			}
		} catch (RejectedExecutionException e) {// 超过最大的服务平台的最大资源限制，无法提供服务
			log(method, version, "超过最大资源限制，无法提供服务。", e);
			RopRequestContext ropRequestContext = buildRequestContextWhenException(request, beginTime);
			RejectedServiceResponse ropResponse = new RejectedServiceResponse(ropRequestContext);
			writeErrorResponse(ropResponse, request, response, jsonpCallback, beginTime);
		} catch (TimeoutException e) {// 服务时间超限
			log(method, version, "服务调用超时。", e);
			TimeoutErrorResponse ropResponse = new TimeoutErrorResponse(method, locale, serviceMethodTimeout);
			writeErrorResponse(ropResponse, request, response, jsonpCallback, beginTime);
		} catch (Exception throwable) {// 产生未知的错误
			log(method, version, "产生异常", throwable);
			ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale,
					throwable);
			writeErrorResponse(ropResponse, request, response, jsonpCallback, beginTime);
		} finally {
			try {
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (IOException e) {
				logger.error("关闭响应出错", e);
			}
		}
	}

	/**
	 * 输出错误信息给调用方
	 * 
	 * @param errorResponse
	 * @param request
	 * @param response
	 * @param jsonpCallback
	 * @param beginTime
	 */
	private void writeErrorResponse(ErrorResponse errorResponse, HttpServletRequest request,
			HttpServletResponse response, String jsonpCallback, long beginTime) {
		MessageFormat format = ServletRequestContextBuilder.getResponseFormat(request);
		writeResponse(errorResponse, response, format, jsonpCallback);
		RopRequestContext ropRequestContext = buildRequestContextWhenException(request, beginTime);
		fireAfterDoServiceEvent(ropRequestContext);
	}

	/**
	 * 记录方法执行日志
	 * 
	 * @param method
	 * @param version
	 * @param msg
	 * @param e
	 */
	private void log(String method, String version, String msg, Exception e) {
		if (logger.isDebugEnabled()) {
			logger.debug("调用服务方法:" + method + "(" + version + ")，" + (msg == null ? "" : msg), e);
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

	/**
	 * 启动平台服务
	 */
	@Override
	public void startup() {
		if (logger.isInfoEnabled()) {
			logger.info("开始启动Rop框架...");
		}
		Assert.notNull(this.applicationContext, "Spring上下文不能为空");
		if (serviceMethodAdapter == null) {
			serviceMethodAdapter = new AnnotationServiceMethodAdapter();
		}
		if (xmlMarshaller == null) {
			xmlMarshaller = new JaxbXmlRopMarshaller();
		}
		if (jsonMarshaller == null) {
			jsonMarshaller = new JacksonJsonRopMarshaller();
		}
		if (sessionManager == null) {
			sessionManager = new DefaultSessionManager();
		}
		if (invokeTimesController == null) {
			invokeTimesController = new DefaultInvokeTimesController();
		}
		// 初始化类型转换器
		if (this.formattingConversionService == null) {
			this.formattingConversionService = getDefaultConversionService();
		}
		registerConverters(formattingConversionService);

		// 实例化ServletRequestContextBuilder
		this.requestContextBuilder = new ServletRequestContextBuilder(this.formattingConversionService);

		// 设置校验器
		if (this.securityManager == null) {
			this.securityManager = new DefaultSecurityManager();
		}

		// 设置异步执行器
		if (this.threadPoolExecutor == null) {
			this.threadPoolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 5 * 60, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
		}

		// 创建Rop上下文
		this.ropContext = buildRopContext();

		// 初始化事件发布器
		this.ropEventMulticaster = buildRopEventMulticaster();

		// 注册会话绑定拦截器
		this.addInterceptor(new SessionBindInterceptor());

		// 初始化信息源
		initMessageSource();

		// 产生Rop框架初始化事件
		fireAfterStartedRopEvent();

		if (logger.isInfoEnabled()) {
			logger.info("Rop框架启动成功！");
		}
	}

	/**
	 * 注册数据转换器
	 * 
	 * @param conversionService
	 */
	private void registerConverters(FormattingConversionService conversionService) {
		conversionService.addConverter(new RopRequestMessageConverter());
		conversionService.addConverter(new UploadFileConverter());
	}

	/**
	 * 创建线程摆渡类实例
	 * 
	 * @return object of ThreadFerry
	 */
	private ThreadFerry buildThreadFerryInstance() {
		if (threadFerryClass != null) {
			return BeanUtils.instantiate(threadFerryClass);
		} else {
			return null;
		}
	}

	/**
	 * 停止服务
	 */
	@Override
	public void shutdown() {
		fireBeforeCloseRopEvent();
		threadPoolExecutor.shutdown();
	}

	/**
	 * 设置是否开启参数签名
	 */
	public void setSignEnable(boolean signEnable) {
		if (!signEnable && logger.isWarnEnabled()) {
			logger.warn("rop close request message sign");
		}
		this.signEnable = signEnable;
	}

	/**
	 * 设置线程变量摆渡类
	 */
	public void setThreadFerryClass(Class<? extends ThreadFerry> threadFerryClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("ThreadFerry set to {}", threadFerryClass.getName());
		}
		this.threadFerryClass = threadFerryClass;
	}

	/**
	 * 设置服务超时控制器
	 */
	public void setInvokeTimesController(InvokeTimesController invokeTimesController) {
		if (logger.isDebugEnabled()) {
			logger.debug("InvokeTimesController set to {}", invokeTimesController.getClass().getName());
		}
		this.invokeTimesController = invokeTimesController;
	}

	/**
	 * 设置服务超时时间
	 */
	public void setServiceTimeoutSeconds(int serviceTimeoutSeconds) {
		if (logger.isDebugEnabled()) {
			logger.debug("serviceTimeoutSeconds set to {}", serviceTimeoutSeconds);
		}
		this.serviceTimeoutSeconds = serviceTimeoutSeconds;
	}

	/**
	 * 设置安全管理对象
	 */
	public void setSecurityManager(SecurityManager securityManager) {
		if (logger.isDebugEnabled()) {
			logger.debug("securityManager set to {}", securityManager.getClass().getName());
		}
		this.securityManager = securityManager;
	}

	/**
	 * 设置消息转换器服务对象
	 */
	public void setFormattingConversionService(FormattingConversionService formatConversionService) {
		if (logger.isDebugEnabled()) {
			logger.debug("formatConversionService set to {}", formatConversionService.getClass().getName());
		}
		this.formattingConversionService = formatConversionService;
	}

	/**
	 * 设置session管理器对象
	 */
	public void setSessionManager(SessionManager sessionManager) {
		if (logger.isDebugEnabled()) {
			logger.debug("sessionManager set to {}", sessionManager.getClass().getName());
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

	/**
	 * 设置扩展的错误消息代码国际化开始名称
	 */
	public void setExtErrorBasename(String extErrorBasename) {
		if (logger.isDebugEnabled()) {
			logger.debug("extErrorBasename set to {}", extErrorBasename);
		}
		this.extErrorBasename = extErrorBasename;
	}

	/**
	 * 设置扩展的错误消息代码国际化开始名称数组
	 */
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
			logger.debug("extErrorBasenames set to {}", Arrays.asList(extErrorBasenames));
		}
	}

	/**
	 * 设置线程池执行器
	 */
	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
		this.threadPoolExecutor = threadPoolExecutor;
		if (logger.isDebugEnabled()) {
			logger.debug("threadPoolExecutor set to {}", threadPoolExecutor.getClass().getName());
			logger.debug("corePoolSize:{}", threadPoolExecutor.getCorePoolSize());
			logger.debug("maxPoolSize:{}", threadPoolExecutor.getMaximumPoolSize());
			logger.debug("keepAliveSeconds:{} seconds", threadPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS));
			logger.debug("queueCapacity:{}", threadPoolExecutor.getQueue().remainingCapacity());
		}
	}

	/**
	 * 设置spring的上下文对象
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 获取rop框架的上下文对象
	 * 
	 * @return object of RopContext
	 */
	public RopContext getRopContext() {
		return this.ropContext;
	}

	/**
	 * 添加服务拦截器
	 */
	public void addInterceptor(Interceptor interceptor) {
		this.interceptors.add(interceptor);
		if (logger.isDebugEnabled()) {
			logger.debug("add  interceptor {}", interceptor.getClass().getName());
		}
	}

	/**
	 * 添加监听器
	 */
	public void addListener(RopEventListener<RopEvent> listener) {
		this.listeners.add(listener);
		if (logger.isDebugEnabled()) {
			logger.debug("add  listener {}", listener.getClass().getName());
		}
	}

	/**
	 * 获取服务超时时间
	 * 
	 * @return 服务超时时间，单位是秒
	 */
	public int getServiceTimeoutSeconds() {
		return serviceTimeoutSeconds > 0 ? serviceTimeoutSeconds : Integer.MAX_VALUE;
	}

	/**
	 * 获取服务方法超时时间，单位是秒
	 *
	 * @param method
	 * @param version
	 * @return 服务方法超时时间，单位是秒
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

	/**
	 * 调用服务线程执行器
	 */
	private class ServiceRunnable implements Runnable {

		private HttpServletRequest servletRequest;
		private HttpServletResponse servletResponse;
		private ThreadFerry threadFerry;
		private RopRequestContext ropRequestContext;

		private ServiceRunnable(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
				ThreadFerry threadFerry) {
			this.servletRequest = servletRequest;
			this.servletResponse = servletResponse;
			this.threadFerry = threadFerry;
		}

		@Override
		public void run() {
			if (threadFerry != null) {
				threadFerry.doInDestThread();
			}
			Object ropRequest;
			try {
				// 用系统级参数构造一个RequestContext实例（第一阶段绑定）
				ropRequestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest, servletResponse);
				// 验证系统级参数的合法性
				MainError mainError = securityManager.validateSystemParameters(ropRequestContext);
				if (mainError != null) {
					ropRequestContext.setRopResponse(new ErrorResponse(mainError));
					return;
				}
				// 绑定业务数据（第二阶段绑定）
				ropRequest = requestContextBuilder.buildRopRequest(ropRequestContext);
				// 进行其它检查业务数据合法性，业务安全等
				mainError = securityManager.validateOther(ropRequestContext);
				if (mainError != null) {
					ropRequestContext.setRopResponse(new ErrorResponse(mainError));
					return;
				}
				firePreDoServiceEvent(ropRequestContext);
				// 服务处理前拦截
				invokeBeforceServiceOfInterceptors(ropRequestContext);
				if (ropRequestContext.getRopResponse() == null) { // 拦截器未生成response
					// 如果拦截器没有产生ropResponse时才调用服务方法
					ropRequestContext.setRopResponse(doService(ropRequest, ropRequestContext));
					// 输出响应前拦截
					invokeBeforceResponseOfInterceptors(ropRequestContext);
				}
			} catch (Exception e) {
				if (ropRequestContext != null) {
					String method = ropRequestContext.getMethod();
					Locale locale = ropRequestContext.getLocale();
					if (logger.isDebugEnabled()) {
						String message = java.text.MessageFormat.format("service {0} call error", method);
						logger.debug(message, e);
					}
					ServiceUnavailableErrorResponse ropResponse = new ServiceUnavailableErrorResponse(method, locale, e);
					// 输出响应前拦截
					invokeBeforceResponseOfInterceptors(ropRequestContext);
					ropRequestContext.setRopResponse(ropResponse);
				} else {
					throw new RopException("RopRequestContext is null.", e);
				}
			} finally {
				if (ropRequestContext != null) {
					// 发布服务完成事件
					ropRequestContext.setServiceEndTime(System.currentTimeMillis());
					// 完成一次服务请求，计算次数
					invokeTimesController.caculateInvokeTimes(ropRequestContext.getAppKey(), ropRequestContext.getSession());
					fireAfterDoServiceEvent(ropRequestContext);
				}
			}
		}

		/**
		 * 调用服务方法实例
		 * @param ropRequest
		 * @param context
		 * @return 服务方法输出消息对象
		 */
		private Object doService(Object ropRequest, RopRequestContext context) {
			Object ropResponse;
			String method = context.getMethod();
			Locale locale = context.getLocale();
			if (method == null) {
				String methodKey = SystemParameterNames.getMethod();
				MainError error = MainErrors.getError(MainErrorType.MISSING_METHOD, locale,	methodKey);
				ropResponse = new ErrorResponse(error);
			} else if (!ropContext.isValidMethod(method)) {
				MainError invalidMethodError = MainErrors.getError(MainErrorType.INVALID_METHOD, locale, method);
				ropResponse = new ErrorResponse(invalidMethodError);
			} else {
				try {
					ropResponse = serviceMethodAdapter.invokeServiceMethod(ropRequest, context);
				} catch (Exception e) { // 出错则导致服务不可用的异常
					if (logger.isInfoEnabled()) {
						logger.info("调用" + context.getMethod() + "时发生异常，异常信息为：" + e.getMessage());
					}
					ropResponse = new ServiceUnavailableErrorResponse(method, locale, e);
				}
			}
			return ropResponse;
		}

		private void firePreDoServiceEvent(RopRequestContext ropRequestContext) {
			ropEventMulticaster.multicastEvent(new PreDoServiceEvent(this, ropRequestContext));
		}

		/**
		 * 在服务调用之后，返回响应之前拦截
		 *
		 * @param ropRequest
		 */
		private void invokeBeforceResponseOfInterceptors(RopRequestContext context) {
			Interceptor tempInterceptor = null;
			try {
				if (interceptors != null && interceptors.isEmpty()) {
					for (Interceptor interceptor : interceptors) {
						tempInterceptor = interceptor;
						interceptor.beforeResponse(context);
					}
				}
			} catch (Exception e) {
				context.setRopResponse(new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale(), e));
				if (tempInterceptor != null) {
					logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
				}
			}
		}

		/**
		 * 在服务调用之前拦截
		 *
		 * @param ropRequestContext
		 */
		private void invokeBeforceServiceOfInterceptors(RopRequestContext ropRequestContext) {
			ObjectHolder<Interceptor> interceptorHolder = new ObjectHolder<Interceptor>();
			try {
				if (interceptors != null && !interceptors.isEmpty()) {
					invokeInterceptors(interceptors, ropRequestContext, interceptorHolder);
				}
				Interceptor tempInterceptor = interceptorHolder.get();
				if (tempInterceptor != null && ropRequestContext.getRopResponse() != null && logger.isDebugEnabled()) {
					logger.debug("拦截器[" + tempInterceptor.getClass().getName() + "]产生了一个RopResponse," + " 阻止本次服务请求继续，服务将直接返回。");
				}
			} catch (Exception e) {
				String method = ropRequestContext.getMethod();
				Locale locale = ropRequestContext.getLocale();
				ropRequestContext.setRopResponse(new ServiceUnavailableErrorResponse(method, locale, e));
				Interceptor tempInterceptor = interceptorHolder.get();
				if(tempInterceptor != null){
					logger.error("在执行拦截器[" + tempInterceptor.getClass().getName() + "]时发生异常.", e);
				}
			}
		}
		
		/**
		 * 调用服务拦截器
		 * @param interceptors
		 * @param ropRequestContext
		 * @return 有输出消息的拦截器对象
		 */
		private void invokeInterceptors(List<Interceptor> interceptors, RopRequestContext ropRequestContext, ObjectHolder<Interceptor> interceptorHolder) {
			for (Interceptor interceptor : interceptors) {
				interceptorHolder.set(interceptor);
				interceptor.beforeService(ropRequestContext);
				// 如果有一个产生了响应，则阻止后续的调用
				if (ropRequestContext.getRopResponse() != null) {
					return;
				}
			}
		}
	}

	private class ObjectHolder<T> {

		private T object;
		
		public T get(){
			return object;
		}
		
		public void set(T object){
			this.object = object;
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
		// 设置异步执行器
		if (threadPoolExecutor != null) {
			simpleRopEventMulticaster.setExecutor(threadPoolExecutor);
		}
		// 添加事件监听器
		if (listeners != null && !listeners.isEmpty()) {
			for (RopEventListener<RopEvent> ropEventListener : listeners) {
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

	/**
	 * 输出消息给服务调用方
	 * @param ropResponse
	 * @param httpServletResponse
	 * @param messageFormat
	 * @param jsonpCallback
	 */
	private void writeResponse(Object ropResponse, HttpServletResponse httpServletResponse, MessageFormat messageFormat,
			String jsonpCallback) {
		try {
			if (!(ropResponse instanceof ErrorResponse) && messageFormat == MessageFormat.STREAM) {
				if (logger.isDebugEnabled()) {
					logger.debug("使用{}输出方式，由服务自身负责响应输出工作.", MessageFormat.STREAM);
				}
				return;
			}
			RopMarshaller ropMarshaller = xmlMarshaller;
			String contentType = APPLICATION_XML;
			if (messageFormat == MessageFormat.JSON) {
				ropMarshaller = jsonMarshaller;
				contentType = APPLICATION_JSON;
			}
			httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			httpServletResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "*");
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

	/**
	 * 设置国际化资源信息
	 */
	private void initMessageSource() {
		HashSet<String> baseNamesSet = new HashSet<String>();
		baseNamesSet.add(I18N_ROP_ERROR);// ROP自动的资源

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

	public List<RopEventListener<RopEvent>> getListeners() {
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

	public void setServiceMethodAdapter(ServiceMethodAdapter serviceMethodAdapter) {
		this.serviceMethodAdapter = serviceMethodAdapter;
	}

	public void setXmlMarshaller(RopMarshaller xmlMarshaller) {
		this.xmlMarshaller = xmlMarshaller;
	}

	public void setJsonMarshaller(RopMarshaller jsonMarshaller) {
		this.jsonMarshaller = jsonMarshaller;
	}
}