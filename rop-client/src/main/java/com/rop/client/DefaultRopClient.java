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
package com.rop.client;

import org.apache.commons.lang.ClassUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rop.MessageFormat;
import com.rop.RopMarshaller;
import com.rop.RopUnmarshaller;
import com.rop.annotation.IgnoreSign;
import com.rop.config.SystemParameterNames;
import com.rop.converter.RopConverter;
import com.rop.converter.UploadFile;
import com.rop.converter.UploadFileConverter;
import com.rop.marshaller.JacksonJsonRopMarshaller;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.unmarshaller.JacksonJsonRopUnmarshaller;
import com.rop.unmarshaller.JaxbXmlRopUnmarshaller;
import com.rop.utils.AnnotationUtils;
import com.rop.utils.Assert;
import com.rop.utils.ReflectionUtils;
import com.rop.utils.RopUtils;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

    //应用密钥
    private String appSecret;

    private String sessionId;

    //报文格式
    private MessageFormat messageFormat = MessageFormat.xml;

    private Locale locale = Locale.SIMPLIFIED_CHINESE;

    private HttpClient httpClient = HttpClients.createSystem();

    private RopUnmarshaller xmlUnmarshaller = new JaxbXmlRopUnmarshaller();

    private RopUnmarshaller jsonUnmarshaller = new JacksonJsonRopUnmarshaller();
    
    private RopMarshaller xmlMarshaller = new JaxbXmlRopMarshaller();
    
    private RopMarshaller jsonMarshaller = new JacksonJsonRopMarshaller();

    //请求类所有请求参数
    private Map<Class<?>, List<Field>> requestAllFields = new HashMap<Class<?>, List<Field>>();

    //请求类所有不需要进行签名的参数
    private Map<Class<?>, List<String>> requestIgnoreSignFieldNames = new HashMap<Class<?>, List<String>>();


    //键为转换的目标类型
    private static Map<Class<?>, RopConverter<String, ?>> ropConverterMap =
            new HashMap<Class<?>, RopConverter<String, ?>>();
    {
        ropConverterMap.put(UploadFile.class, new UploadFileConverter());
    }

    public DefaultRopClient(String serverUrl, String appKey, String appSecret) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public DefaultRopClient(String serverUrl, String appKey, String appSecret, MessageFormat messageFormat) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.messageFormat = messageFormat;
    }

    public DefaultRopClient(String serverUrl, String appKey, String appSecret, MessageFormat messageFormat, Locale locale) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
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
        SystemParameterNames.setAppKey(paramName);
        return this;
    }


    public RopClient setSessionIdParamName(String paramName) {
        SystemParameterNames.setSessionId(paramName);
        return this;
    }


    public RopClient setMethodParamName(String paramName) {
        SystemParameterNames.setMethod(paramName);
        return this;
    }


    public RopClient setVersionParamName(String paramName) {
        SystemParameterNames.setVersion(paramName);
        return this;
    }


    public RopClient setFormatParamName(String paramName) {
        SystemParameterNames.setFormat(paramName);
        return this;
    }


    public RopClient setLocaleParamName(String paramName) {
        SystemParameterNames.setLocale(paramName);
        return this;
    }


    public RopClient setSignParamName(String paramName) {
        SystemParameterNames.setSign(paramName);
        return this;
    }


    public void addRopConvertor(RopConverter<String, ?> ropConverter) {
    	DefaultRopClient.ropConverterMap.put(ropConverter.getTargetClass(), ropConverter);
    }


    public ClientRequest buildClientRequest() {
        return new DefaultClientRequest(this);
    }

    private class DefaultClientRequest implements ClientRequest {

        private Map<String, String> paramMap = new HashMap<String, String>(20);

        private List<String> ignoreSignParams = new ArrayList<String>();

        private DefaultClientRequest(RopClient ropClient) {
            paramMap.put(SystemParameterNames.getAppKey(), appKey);
            paramMap.put(SystemParameterNames.getFormat(), messageFormat.name());
            paramMap.put(SystemParameterNames.getLocale(), locale.toString());
            if (sessionId != null) {
                paramMap.put(SystemParameterNames.getSessionId(), sessionId);
            }
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

        private <T> CompositeResponse<T> post(Class<T> ropResponseClass, Map<String, String> requestParams) throws ParseException, IOException {
        	HttpPost httpPost = new HttpPost(serverUrl);
        	httpPost.setEntity(toHttpEntity(requestParams));
        	HttpResponse response = httpClient.execute(httpPost);
        	if (logger.isDebugEnabled()) {
                logger.debug("response:\n" + response);
            }
            String responseContent = EntityUtils.toString(response.getEntity());
            return toCompositeResponse(responseContent, ropResponseClass);
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
        	HttpGet httpGet = new HttpGet(buildGetUrl(requestParams));
        	HttpResponse response = httpClient.execute(httpGet);
        	if (logger.isDebugEnabled()) {
                logger.debug("response:\n" + response);
            }
            String responseContent = EntityUtils.toString(response.getEntity());
            return toCompositeResponse(responseContent, ropResponseClass);
        }

        private Map<String, String> addOtherParamMap(String methodName, String version) {
            paramMap.put(SystemParameterNames.getMethod(), methodName);
            paramMap.put(SystemParameterNames.getVersion(), version);
            String signValue = RopUtils.sign(paramMap, ignoreSignParams, appSecret);
            paramMap.put(SystemParameterNames.getSign(), signValue);
            return paramMap;
        }

        private <T> CompositeResponse<T> toCompositeResponse(String content, Class<T> ropResponseClass) {
            if(logger.isDebugEnabled()){
                logger.debug(content);
            }
            boolean successful = isSuccessful(content);
            DefaultCompositeResponse<T> compositeResponse = new DefaultCompositeResponse<T>(successful);

            if (MessageFormat.json == messageFormat) {
                if (successful) {
                    T ropResponse = jsonUnmarshaller.unmarshaller(content, ropResponseClass);
                    compositeResponse.setSuccessRopResponse(ropResponse);
                } else {
                    ErrorResponse errorResponse = jsonUnmarshaller.unmarshaller(content, ErrorResponse.class);
                    compositeResponse.setErrorResponse(errorResponse);
                }
            } else {
                if (successful) {
                    T ropResponse = xmlUnmarshaller.unmarshaller(content, ropResponseClass);
                    compositeResponse.setSuccessRopResponse(ropResponse);
                } else {
                    ErrorResponse errorResponse = xmlUnmarshaller.unmarshaller(content, ErrorResponse.class);
                    compositeResponse.setErrorResponse(errorResponse);
                }
            }
            return compositeResponse;
        }

        private boolean isSuccessful(String content) {
            return !(content.contains(CommonConstant.ERROR_TOKEN));
        }

        private String buildGetUrl(Map<String, String> form) {
            StringBuilder requestUrl = new StringBuilder();
            requestUrl.append(serverUrl);
            requestUrl.append("?");
            String joinChar = "";
            for (Map.Entry<String, String> entry : form.entrySet()) {
                requestUrl.append(joinChar);
                requestUrl.append(entry.getKey());
                requestUrl.append("=");
                requestUrl.append(entry.getValue());
                joinChar = "&";
            }
            return requestUrl.toString();
        }

        private Map<String, String> getRequestForm(Object ropRequest, String methodName, String version) throws IOException {

            Map<String, String> form = new LinkedHashMap<String, String>(16);

            //系统级参数
            form.put(SystemParameterNames.getAppKey(), appKey);
            form.put(SystemParameterNames.getMethod(), methodName);
            form.put(SystemParameterNames.getVersion(), version);
            form.put(SystemParameterNames.getFormat(), messageFormat.name());
            form.put(SystemParameterNames.getLocale(), locale.toString());
            if (sessionId != null) {
                form.put(SystemParameterNames.getSessionId(), sessionId);
            }

            //业务级参数
            form.putAll(getParamFields(ropRequest, messageFormat));

            //对请求进行签名
            String signValue = sign(ropRequest.getClass(), appSecret, form);
            form.put("sign", signValue);
            return form;
        }

        private HttpEntity toHttpEntity(Map<String, String> form) {
            List<NameValuePair> list = new ArrayList<NameValuePair>(form.size());
            for (Map.Entry<String, String> entry : form.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            EntityBuilder builder = EntityBuilder.create();
            builder.setParameters(list);
            return builder.build();
        }

        /**
         * 对请求参数进行签名
         *
         * @param ropRequestClass
         * @param appSecret
         * @param form
         * @return
         */
        private String sign(Class<?> ropRequestClass, String appSecret, Map<String, String> form) {
            List<String> ignoreFieldNames = requestIgnoreSignFieldNames.get(ropRequestClass);
            return RopUtils.sign(form, ignoreFieldNames, appSecret);
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
                    } else if (field.getType().isAnnotationPresent(XmlRootElement.class) ||
                            field.getType().isAnnotationPresent(XmlType.class)) {
                        String message;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        if(MessageFormat.json == mf){
                        	jsonMarshaller.marshaller(fieldValue, baos);
                        	message = baos.toString("UTF-8");
                        }else{
                        	xmlMarshaller.marshaller(fieldValue, baos);
                        	message = baos.toString("UTF-8");
                        }
                        params.put(field.getName(), message);
                    } else {
                        params.put(field.getName(), fieldValue.toString());
                    }
                }
            }
            return params;
        }
    }

    private RopConverter<String, ?> getConvertor(Class<?> fieldType) {
        for (Class<?> aClass : ropConverterMap.keySet()) {
            if (ClassUtils.isAssignable(aClass, fieldType)) {
                return ropConverterMap.get(aClass);
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

