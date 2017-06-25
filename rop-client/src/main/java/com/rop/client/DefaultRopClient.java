/*
 * Copyright 2012-2017 the original author or authors.
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
package com.rop.client;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rop.MessageFormat;
import com.rop.RopMarshaller;
import com.rop.RopUnmarshaller;
import com.rop.annotation.IgnoreSign;
import com.rop.client.http.HttpClient;
import com.rop.client.http.HttpResponse;
import com.rop.client.http.JdkHttpClient;
import com.rop.config.SystemParameterNames;
import com.rop.converter.RopConverter;
import com.rop.converter.UploadFile;
import com.rop.converter.UploadFileConverter;
import com.rop.marshaller.FastjsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.sign.SignHandler;
import com.rop.unmarshaller.FastjsonRopUnmarshaller;
import com.rop.unmarshaller.JaxbXmlRopUnmarshaller;
import com.rop.utils.AnnotationUtils;
import com.rop.utils.Assert;
import com.rop.utils.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopClient implements RopClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    //服务地址
    private String serverUrl;

    //应用键
    private String appKey;

    private String sessionId;
    
    private String appKeyName = SystemParameterNames.getAppKey();
    
    private String sessionIdName = SystemParameterNames.getSessionId();
    
    private String methodName = SystemParameterNames.getMethod();
    
    private String versionName = SystemParameterNames.getVersion();
    
    private String formatName = SystemParameterNames.getFormat();
    
    private String localeName = SystemParameterNames.getLocale();
    
    private String signName = SystemParameterNames.getSign();
    
    private SignHandler signHandler;

    //报文格式
    private MessageFormat messageFormat = MessageFormat.JSON;

    private Locale locale = Locale.SIMPLIFIED_CHINESE;

    private HttpClient httpClient = new JdkHttpClient();

    private RopUnmarshaller xmlUnmarshaller;

    private RopUnmarshaller jsonUnmarshaller;
    
    private RopMarshaller xmlMarshaller;
    
    private RopMarshaller jsonMarshaller;

    //请求类所有请求参数
    private Map<Class<?>, List<Field>> requestAllFields = new HashMap<Class<?>, List<Field>>();

    //请求类所有不需要进行签名的参数
    private Map<Class<?>, List<String>> requestIgnoreSignFieldNames = new HashMap<Class<?>, List<String>>();

    //键为转换的目标类型
    private static Map<Class<?>, RopConverter<String, ?>> ropConverterMap = new HashMap<Class<?>, RopConverter<String, ?>>();
   
    static {
        ropConverterMap.put(UploadFile.class, new UploadFileConverter());
    }

    public DefaultRopClient(String serverUrl, String appKey) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
    }

    public DefaultRopClient(String serverUrl, String appKey, MessageFormat messageFormat) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.messageFormat = messageFormat;
    }

    public DefaultRopClient(String serverUrl, String appKey, MessageFormat messageFormat, Locale locale) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.messageFormat = messageFormat;
        this.locale = locale;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public RopClient setAppKeyParamName(String paramName) {
    	this.appKeyName = paramName;
        return this;
    }

    public RopClient setSessionIdParamName(String paramName) {
        this.sessionIdName = paramName;
        return this;
    }

    public RopClient setMethodParamName(String paramName) {
    	this.methodName = paramName;
        return this;
    }

    public RopClient setVersionParamName(String paramName) {
        this.versionName = paramName;
        return this;
    }

    public RopClient setFormatParamName(String paramName) {
        this.formatName = paramName;
        return this;
    }

    public RopClient setLocaleParamName(String paramName) {
        this.localeName = paramName;
        return this;
    }

    public RopClient setSignParamName(String paramName) {
    	this.signName = paramName;
        return this;
    }
    
    public RopClient setSignHandler(SignHandler handler) {
    	this.signHandler = handler;
    	return this;
    }

    public void addRopConvertor(RopConverter<String, ?> ropConverter) {
    	DefaultRopClient.ropConverterMap.put(ropConverter.getTargetClass(), ropConverter);
    }

    public ClientRequest buildClientRequest() {
        return new DefaultClientRequest();
    }

	public RopUnmarshaller getXmlUnmarshaller() {
		if(xmlUnmarshaller == null){
			xmlUnmarshaller = new JaxbXmlRopUnmarshaller();
		}
		return xmlUnmarshaller;
	}

	public void setXmlUnmarshaller(RopUnmarshaller xmlUnmarshaller) {
		this.xmlUnmarshaller = xmlUnmarshaller;
	}

	public RopUnmarshaller getJsonUnmarshaller() {
		if(jsonUnmarshaller == null){
			jsonUnmarshaller = new FastjsonRopUnmarshaller();
		}
		return jsonUnmarshaller;
	}

	public void setJsonUnmarshaller(RopUnmarshaller jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}

	public RopMarshaller getXmlMarshaller() {
		if(xmlMarshaller == null){
			xmlMarshaller = new JaxbXmlRopMarshaller();
		}
		return xmlMarshaller;
	}

	public void setXmlMarshaller(RopMarshaller xmlMarshaller) {
		this.xmlMarshaller = xmlMarshaller;
	}

	public RopMarshaller getJsonMarshaller() {
		if(jsonMarshaller == null){
			jsonMarshaller = new FastjsonRopMarshaller();
		}
		return jsonMarshaller;
	}

	public void setJsonMarshaller(RopMarshaller jsonMarshaller) {
		this.jsonMarshaller = jsonMarshaller;
	}
	
    private class DefaultClientRequest implements ClientRequest {

        private Map<String, String> paramMap = new HashMap<String, String>(20);

        private List<String> ignoreSignParams = new ArrayList<String>();
        
        private Map<String, String> headMap = new HashMap<String, String>();

        private DefaultClientRequest() {
            paramMap.put(appKeyName, appKey);
            paramMap.put(formatName, messageFormat.name());
            paramMap.put(localeName, locale.toString());
            if (sessionId != null) {
                paramMap.put(sessionIdName, sessionId);
            }
        }

    	/**
    	 * 设置http请求头信息
    	 * @param name
    	 * @param value
    	 * @return ClientRequest
    	 */
    	public ClientRequest setHeader(String name, String value){
    		headMap.put(name, value);
    		return this;
    	}

        public ClientRequest addParam(String paramName, Object paramValue) {
            addParam(paramName,paramValue,false);
            return this;
        }

        public ClientRequest clearParam() {
            paramMap.clear();
            return this;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
		public ClientRequest addParam(String paramName, Object paramValue, boolean ignoreSign) {
            Assert.isTrue(paramName != null && paramName.length() > 0, "参数名不能为空");
            Assert.notNull(paramValue, "参数值不能为null");

            //将参数添加到参数列表中
            String valueAsStr = paramValue.toString();
            if (ropConverterMap.containsKey(paramValue.getClass())) {
                RopConverter ropConverter = ropConverterMap.get(paramValue.getClass());
                valueAsStr = (String) ropConverter.unconvert(paramValue);
            }
            paramMap.put(paramName, valueAsStr);
            IgnoreSign typeIgnore = AnnotationUtils.findAnnotation(paramValue.getClass(), IgnoreSign.class);
            if (ignoreSign || typeIgnore != null) {
                ignoreSignParams.add(paramName);
            }
            return this;
        }

        public <T> CompositeResponse<T> post(Class<T> ropResponseClass, String methodName, String version) throws IOException {
            Map<String, String> requestParams = addOtherParamMap(methodName, version);
            return post(ropResponseClass, requestParams);
        }

        public <T> CompositeResponse<T> post(Object ropRequest, Class<T> ropResponseClass, String methodName, String version) throws IOException {
            Map<String, String> requestParams = getRequestForm(ropRequest, methodName, version);
            return post(ropResponseClass, requestParams);
        }

        private <T> CompositeResponse<T> post(Class<T> ropResponseClass, Map<String, String> requestParams) throws IOException {
        	HttpResponse response = httpClient.post(headMap, serverUrl, requestParams);
			if(response.isSuccessful()){
				String responseContent = response.getString();
				return toCompositeResponse(responseContent, ropResponseClass);
			}
			return new DefaultCompositeResponse<T>(false);
        }


        public <T> CompositeResponse<T> get(Class<T> ropResponseClass, String methodName, String version) throws IOException {
            Map<String, String> requestParams = addOtherParamMap(methodName, version);
            return get(ropResponseClass, requestParams);
        }

        public <T> CompositeResponse<T> get(Object ropRequest, Class<T> ropResponseClass, String methodName, String version) throws IOException {
            Map<String, String> requestParams = getRequestForm(ropRequest, methodName, version);
            return get(ropResponseClass, requestParams);
        }

        private <T> CompositeResponse<T> get(Class<T> ropResponseClass, Map<String, String> requestParams) throws IOException {
        	HttpResponse response = httpClient.get(headMap, serverUrl, requestParams);
			if(response.isSuccessful()){
				String responseContent = response.getString();
				return toCompositeResponse(responseContent, ropResponseClass);
			}
			return new DefaultCompositeResponse<T>(false);
        }

        private Map<String, String> addOtherParamMap(String methodName, String version) {
            paramMap.put(DefaultRopClient.this.methodName, methodName);
            paramMap.put(DefaultRopClient.this.versionName, version);
            String signValue = DefaultRopClient.this.signHandler.sign(paramMap, ignoreSignParams);
            paramMap.put(DefaultRopClient.this.signName, signValue);
            return paramMap;
        }

        private <T> CompositeResponse<T> toCompositeResponse(String content, Class<T> ropResponseClass) {
            if(logger.isDebugEnabled()){
                logger.debug(content);
            }
            boolean successful = isSuccessful(content);
            DefaultCompositeResponse<T> compositeResponse = new DefaultCompositeResponse<T>(successful);
            if (MessageFormat.JSON == messageFormat) {
                if (successful) {
                    T ropResponse = getJsonUnmarshaller().unmarshaller(content, ropResponseClass);
                    compositeResponse.setSuccessRopResponse(ropResponse);
                } else {
                    ErrorResponse errorResponse = getJsonUnmarshaller().unmarshaller(content, ErrorResponse.class);
                    compositeResponse.setErrorResponse(errorResponse);
                }
            } else {
                if (successful) {
                    T ropResponse = getXmlUnmarshaller().unmarshaller(content, ropResponseClass);
                    compositeResponse.setSuccessRopResponse(ropResponse);
                } else {
                    ErrorResponse errorResponse = getXmlUnmarshaller().unmarshaller(content, ErrorResponse.class);
                    compositeResponse.setErrorResponse(errorResponse);
                }
            }
            return compositeResponse;
        }

        private boolean isSuccessful(String content) {
            return !(content.contains(CommonConstant.ERROR_TOKEN));
        }

        private Map<String, String> getRequestForm(Object ropRequest, String methodName, String version) throws IOException {
            Map<String, String> form = new LinkedHashMap<String, String>(16);
            //系统级参数
            form.put(DefaultRopClient.this.appKeyName, appKey);
            form.put(DefaultRopClient.this.methodName, methodName);
            form.put(DefaultRopClient.this.versionName, version);
            form.put(DefaultRopClient.this.formatName, messageFormat.name());
            form.put(DefaultRopClient.this.localeName, locale.toString());
            if (sessionId != null) {
                form.put(DefaultRopClient.this.sessionIdName, sessionId);
            }

            //业务级参数
            form.putAll(getParamFields(ropRequest, messageFormat));

            //对请求进行签名
            String signValue = sign(ropRequest.getClass(), form);
            form.put(DefaultRopClient.this.signName, signValue);
            return form;
        }


        /**
         * 对请求参数进行签名
         *
         * @param ropRequestClass
         * @param appSecret
         * @param form
         * @return
         */
        private String sign(Class<?> ropRequestClass, Map<String, String> form) {
            List<String> ignoreFieldNames = requestIgnoreSignFieldNames.get(ropRequestClass);
            return DefaultRopClient.this.signHandler.sign(form, ignoreFieldNames);
        }

        /**
         * 获取ropRequest对应的参数名列表
         *
         * @param ropRequest
         * @param mf
         * @return
         * @throws IOException 
         */
        private Map<String, String> getParamFields(Object ropRequest, MessageFormat mf) throws IOException {
            if (!requestAllFields.containsKey(ropRequest.getClass())) {
                parseRopRequestClass(ropRequest);
            }
            return toParamValueMap(ropRequest, mf);
        }

        /**
         * 获取ropRequest对象的对应的参数列表
         *
         * @param ropRequest
         * @param mf
         * @return
         * @throws IOException 
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
		private Map<String, String> toParamValueMap(Object ropRequest, MessageFormat mf) throws IOException {
            List<Field> fields = requestAllFields.get(ropRequest.getClass());
            Map<String, String> params = new HashMap<String, String>();
            for (Field field : fields) {
                RopConverter convertor = getConvertor(field.getType());
                Object fieldValue = ReflectionUtils.getField(field, ropRequest);
                if (fieldValue != null) {
                    if (convertor != null) {//有对应转换器
                        String strParamValue = (String) convertor.unconvert(fieldValue);
                        params.put(field.getName(), strParamValue);
                    } else if (!field.getType().isPrimitive()) {
                        String message = marshaller(fieldValue, mf);
                        params.put(field.getName(), message);
                    } else {
                        params.put(field.getName(), fieldValue.toString());
                    }
                }
            }
            return params;
        }
        

        private String marshaller(Object value, MessageFormat mf) throws IOException{
        	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	 String message;
             if(MessageFormat.JSON == mf){
             	getJsonMarshaller().marshaller(value, baos);
             	message = baos.toString("UTF-8");
             }else{
             	getXmlMarshaller().marshaller(value, baos);
             	message = baos.toString("UTF-8");
             }
             return message;
        }

        private RopConverter<String, ?> getConvertor(Class<?> fieldType) {
            for (Map.Entry<Class<?>,RopConverter<String,?>> entry : ropConverterMap.entrySet()) {
                if (ClassUtils.isAssignable(entry.getKey(), fieldType)) {
                    return entry.getValue();
                }
            }
            return null;
        }

        private void parseRopRequestClass(Object ropRequest) {
            List<Field> allFields = ReflectionUtils.getFields(ropRequest.getClass());
            List<String> ignoreSignFieldNames = ReflectionUtils.getIgnoreSignFieldNames(ropRequest.getClass());
            requestAllFields.put(ropRequest.getClass(), allFields);
            requestIgnoreSignFieldNames.put(ropRequest.getClass(), ignoreSignFieldNames);
        }
    }
}

