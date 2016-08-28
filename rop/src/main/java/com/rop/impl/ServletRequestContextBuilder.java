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
import com.rop.annotation.HttpAction;
import com.rop.config.SystemParameterNames;
import com.rop.response.MainErrorType;
import com.rop.security.MainErrors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <pre>
 *    构建{@link com.rop.RopRequestContext}实例
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServletRequestContextBuilder implements RequestContextBuilder {

    //通过前端的负载均衡服务器时，请求对象中的IP会变成负载均衡服务器的IP，因此需要特殊处理，下同。
    public static final String X_REAL_IP = "X-Real-IP";

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private FormattingConversionService conversionService;

    private Validator validator;

    public ServletRequestContextBuilder(FormattingConversionService conversionService) {
        this.conversionService = conversionService;
    }


    public SimpleRopRequestContext buildBySysParams(RopContext ropContext,
    		HttpServletRequest request, HttpServletResponse response) {
        SimpleRopRequestContext requestContext = new SimpleRopRequestContext(ropContext);

        //设置请求对象及参数列表
        requestContext.setRawRequestObject(request);
        if (response != null) {
            requestContext.setRawResponseObject(response);
        }
        requestContext.setAllParams(getRequestParams(request));
        requestContext.setIp(getRemoteAddr(request)); //感谢melin所指出的BUG

        //设置服务的系统级参数
        requestContext.setAppKey(request.getParameter(SystemParameterNames.getAppKey()));
        requestContext.setSessionId(request.getParameter(SystemParameterNames.getSessionId()));
        requestContext.setMethod(request.getParameter(SystemParameterNames.getMethod()));
        requestContext.setVersion(request.getParameter(SystemParameterNames.getVersion()));
        requestContext.setLocale(getLocale(request));
        requestContext.setFormat(getFormat(request));
        requestContext.setMessageFormat(getResponseFormat(request));
        requestContext.setSign(request.getParameter(SystemParameterNames.getSign()));
        requestContext.setHttpAction(HttpAction.fromValue(request.getMethod()));

        //设置服务处理器
        ServiceMethodHandler serviceMethodHandler =
                ropContext.getServiceMethodHandler(requestContext.getMethod(), requestContext.getVersion());
        requestContext.setServiceMethodHandler(serviceMethodHandler);

        return requestContext;
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String remoteIp = request.getHeader(X_REAL_IP); //nginx反向代理
        if (StringUtils.hasText(remoteIp)) {
            return remoteIp;
        } else {
            remoteIp = request.getHeader(X_FORWARDED_FOR);//apache反射代理
            if (StringUtils.hasText(remoteIp)) {
                String[] ips = remoteIp.split(",");
                for (String ip : ips) {
                    if (!"null".equalsIgnoreCase(ip)) {
                        return ip;
                    }
                }
            }
            return request.getRemoteAddr();
        }
    }

    /**
     * 将{@link HttpServletRequest}的数据绑定到{@link com.rop.RopRequestContext}的{@link com.rop.RopRequest}中，同时使用
     * JSR 303对请求数据进行校验，将错误信息设置到{@link com.rop.RopRequestContext}的属性列表中。
     *
     * @param ropRequestContext
     */
    public Object buildRopRequest(RopRequestContext ropRequestContext) {
    	ServiceMethodHandler methodHandler = ropRequestContext.getServiceMethodHandler();
    	Class<?>[] classes = methodHandler.getHandlerMethod().getParameterTypes();
    	Object[] args = new Object[classes.length];
    	if(classes.length < 1){
    		return args;
    	}
    	HttpServletRequest request = ropRequestContext.getRawRequestObject();
    	List<ObjectError> errors = new ArrayList<ObjectError>();
    	for(int i = 0; i < args.length; i++){
    		Class<?> clazz = classes[i];
    		if(HttpServletRequest.class.isAssignableFrom(clazz)){
    			args[i] = request;
    		}else if(RopRequestContext.class.isAssignableFrom(clazz)){
    			args[i] = ropRequestContext;
    		}else if(RopContext.class.isAssignableFrom(clazz)){
    			args[i] = ropRequestContext.getRopContext();
    		}else if(HttpServletResponse.class.isAssignableFrom(clazz)){
    			args[i] = ropRequestContext.getRopResponse();
    		}else{
    			BindingResult bindingResult = doBind(request, clazz);
    			args[i] = buildRopRequestFromBindingResult(ropRequestContext, bindingResult);
    			List<ObjectError> allErrors = bindingResult.getAllErrors();
    			if(allErrors != null && allErrors.size() > 0){
    				errors.addAll(allErrors);
    			}
    		}
    	}
        ropRequestContext.setAttribute(SimpleRopRequestContext.SPRING_VALIDATE_ERROR_ATTRNAME, errors.isEmpty() ? null : errors);
        return args;
    }


    private String getFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SystemParameterNames.getFormat());
        if (messageFormat == null) {
            return MessageFormat.xml.name();
        } else {
            return messageFormat;
        }
    }

    public static Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(SystemParameterNames.getLocale()) != null) {
            try {
                LocaleEditor localeEditor = new LocaleEditor();
                localeEditor.setAsText(webRequest.getParameter(SystemParameterNames.getLocale()));
                Locale locale = (Locale) localeEditor.getValue();
                if (isValidLocale(locale)) {
                    return locale;
                }
            } catch (Exception e) {
                return Locale.SIMPLIFIED_CHINESE;
            }
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    private static boolean isValidLocale(Locale locale) {
        if (Locale.SIMPLIFIED_CHINESE.equals(locale) || Locale.ENGLISH.equals(locale)) {
            return true;
        } else {
            try {
                //check error resource file exists
                MainErrors.getError(MainErrorType.INVALID_APP_KEY, locale);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    public static MessageFormat getResponseFormat(HttpServletRequest servletRequest) {
        String messageFormat = servletRequest.getParameter(SystemParameterNames.getFormat());
        if (MessageFormat.isValidFormat(messageFormat)) {
            return MessageFormat.getFormat(messageFormat);
        } else {
            return MessageFormat.xml;
        }
    }

    private Object buildRopRequestFromBindingResult(RopRequestContext ropRequestContext, BindingResult bindingResult) {
    	Object ropRequest = bindingResult.getTarget();
    	if(ropRequest instanceof RopRequest){
    		((RopRequest)ropRequest).setRopRequestContext(ropRequestContext);
    	}
        return ropRequest;
    }

    private HashMap<String, String> getRequestParams(HttpServletRequest request) {
        @SuppressWarnings("rawtypes")
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

    private BindingResult doBind(HttpServletRequest webRequest, Class<? extends Object> requestType) {
    	Object bindObject = BeanUtils.instantiateClass(requestType);
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(bindObject, "bindObject");
        dataBinder.setConversionService(getFormattingConversionService());
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

    public FormattingConversionService getFormattingConversionService() {
        return conversionService;
    }
}

