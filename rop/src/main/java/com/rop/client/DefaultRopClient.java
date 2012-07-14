/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-30
 */
package com.rop.client;

import com.rop.MessageFormat;
import com.rop.RopRequest;
import com.rop.RopResponse;
import com.rop.annotation.IgnoreSign;
import com.rop.annotation.Temporary;
import com.rop.client.unmarshaller.JacksonJsonRopUnmarshaller;
import com.rop.client.unmarshaller.JaxbXmlRopUnmarshaller;
import com.rop.config.SystemParameterNames;
import com.rop.response.ErrorResponse;
import com.rop.utils.RopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
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

    //报文格式
    private String format = MessageFormat.xml.name();

    private String locale = "zh_CN";

    private RestTemplate restTemplate = new RestTemplate();

    private RopUnmarshaller xmlUnmarshaller = new JaxbXmlRopUnmarshaller();

    private RopUnmarshaller jsonUnmarshaller = new JacksonJsonRopUnmarshaller();

    //请求类所有请求参数
    private Map<Class<?>, List<Field>> requestAllFields = new HashMap<Class<?>, List<Field>>();

    //请求类所有不需要进行签名的参数
    private Map<Class<?>, List<String>> requestIgnoreSignFieldNames = new HashMap<Class<?>, List<String>>();

    public DefaultRopClient(String serverUrl, String appKey, String appSecret) {
        this.serverUrl = serverUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public <T extends RopResponse> CompositeResponse get(RopRequest ropRequest, Class<T> ropResponseClass,
                                                         String methodName, String version) {
        Map<String, String> form = getRequestForm(ropRequest, methodName, version);
        String requestUrl = buildGetUrl(form);
        String responseContent = restTemplate.getForObject(requestUrl, String.class);
        if (logger.isDebugEnabled()) {
            logger.debug("response:\n" + responseContent);
        }
        return toCompositeResponse(responseContent, ropResponseClass);
    }

    @Override
    public <T extends RopResponse> CompositeResponse get(RopRequest ropRequest, Class<T> ropResponseClass,
                                                         String methodName, String version, String sessionId) {
        return get(ropRequest, ropResponseClass, methodName, version, null);
    }

    @Override
    public <T extends RopResponse> CompositeResponse post(RopRequest ropRequest, Class<T> ropResponseClass,
                                                          String methodName, String version, String sessionId) {
        Map<String, String> form = getRequestForm(ropRequest, methodName, version, sessionId);
        String responseContent = restTemplate.postForObject(serverUrl, toMultiValueMap(form), String.class);
        if (logger.isDebugEnabled()) {
            logger.debug("response:\n" + responseContent);
        }
        return toCompositeResponse(responseContent, ropResponseClass);
    }

    @Override
    public <T extends RopResponse> CompositeResponse post(RopRequest ropRequest, Class<T> ropResponseClass,
                                                          String methodName, String version) {
        return post(ropRequest, ropResponseClass, methodName, version, null);
    }

    private <T extends RopResponse> CompositeResponse toCompositeResponse(String content, Class<T> ropResponseClass) {
        boolean successful = isSuccessful(content);
        DefaultCompositeResponse<T> compositeResponse = new DefaultCompositeResponse<T>(successful);

        if (MessageFormat.json.name().equalsIgnoreCase(this.format)) {
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
        if (MessageFormat.json.name().equalsIgnoreCase(this.format)) {
            return !(content.contains("{\"error\"") && content.contains("\"code\":"));
        } else {
            return !(content.contains("<error") && content.contains("code=\""));
        }
    }

    private Map<String, String> getRequestForm(RopRequest ropRequest, String methodName, String version) {
        return getRequestForm(ropRequest, methodName, version, null);
    }

    private Map<String, String> getRequestForm(RopRequest ropRequest, String methodName, String version, String sessionId) {

        Map<String, String> form = new LinkedHashMap<String, String>(16);

        //系统级参数
        form.put(SystemParameterNames.getAppKey(), appKey);
        form.put(SystemParameterNames.getMethod(), methodName);
        form.put(SystemParameterNames.getVersion(), version);
        form.put(SystemParameterNames.getFormat(), format);
        form.put(SystemParameterNames.getLocale(), locale);
        if (sessionId != null) {
            form.put(SystemParameterNames.getSessionId(), sessionId);
        }

        //业务级参数
        form.putAll(getParamFields(ropRequest));

        //对请求进行签名
        String signValue = sign(ropRequest.getClass(), appSecret, form);
        form.put("sign", signValue);
        return form;

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
            joinChar ="&";
        }
        return requestUrl.toString();
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
        if (ignoreFieldNames != null && ignoreFieldNames.size() > 0) {
            HashMap<String, String> needSignParams = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : form.entrySet()) {
                if (!ignoreFieldNames.contains(entry.getKey())) {
                    needSignParams.put(entry.getKey(), entry.getValue());
                }
            }
            return RopUtils.sign(needSignParams, appSecret);
        } else {
            return RopUtils.sign(form, appSecret);
        }
    }

    private Map<String, String> getParamFields(RopRequest ropRequest) {
        if (!requestAllFields.containsKey(ropRequest.getClass())) {
            final ArrayList<Field> allFields = new ArrayList<Field>();
            final ArrayList<String> ignoreSignFieldNames = new ArrayList<String>();
            ReflectionUtils.doWithFields(ropRequest.getClass(), new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    ReflectionUtils.makeAccessible(field);
                    if (!isTemporaryField(field)) {
                        allFields.add(field);
                        if (isIgoreSignField(field)) {
                            ignoreSignFieldNames.add(field.getName());
                        }
                    }
                }

                private boolean isTemporaryField(Field field) {
                    Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                    if (declaredAnnotations != null) {
                        for (Annotation declaredAnnotation : declaredAnnotations) {
                            if (declaredAnnotation.equals(Temporary.class)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                private boolean isIgoreSignField(Field field) {
                    Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                    if (declaredAnnotations != null) {
                        for (Annotation declaredAnnotation : declaredAnnotations) {
                            if(ClassUtils.isAssignableValue(IgnoreSign.class,declaredAnnotation)){
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });

            requestAllFields.put(ropRequest.getClass(), allFields);
            requestIgnoreSignFieldNames.put(ropRequest.getClass(), ignoreSignFieldNames);
        }

        List<Field> fields = requestAllFields.get(ropRequest.getClass());
        Map<String, String> params = new HashMap<String, String>();
        for (Field field : fields) {
            Object fieldValue = ReflectionUtils.getField(field, ropRequest);
            if (fieldValue != null) {
                params.put(field.getName(), fieldValue.toString());
            }
        }
        return params;
    }


    private MultiValueMap<String, String> toMultiValueMap(Map<String, String> form) {
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
        for (Map.Entry<String, String> entry : form.entrySet()) {
            mvm.add(entry.getKey(), entry.getValue());
        }
        return mvm;
    }
}

