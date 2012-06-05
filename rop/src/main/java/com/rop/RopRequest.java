/**
 *
 * 日    期：12-2-10
 */
package com.rop;

import com.rop.annotation.IgnoreSign;

import java.util.HashMap;
import java.util.Locale;

/**
 * <pre>
 *    BOP服务的请求对象，请求方法的入参必须是继承于该类。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopRequest {

    //请求方法
    private String method;

    //版本号
    private String version = "1.0";

    //会话ID
    private String sessionId;

    //应用键
    private String appKey;

    //签名
    @IgnoreSign
    private String sign;

    //格式
    private MessageFormat msgFormat = MessageFormat.xml;

    //本地化
    private Locale locale = Locale.SIMPLIFIED_CHINESE;

    //客户端IP
    private String ip;

    /**
     * 原始的请求对象
     */
    private Object rawRequestObject;

    /**
     * 请求参数列表
     */
    private HashMap<String, String> paramValues;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public MessageFormat getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(MessageFormat msgFormat) {
        this.msgFormat = msgFormat;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取请求参数列表
     *
     * @return
     */
    public HashMap<String, String> getParamValues() {
        return paramValues;
    }

    /**
     * 设置请求参数列表
     *
     * @param paramValues
     */
    public void setParamValues(HashMap<String, String> paramValues) {
        this.paramValues = paramValues;
    }

    /**
     * 获取参数对应的值
     *
     * @param paramName
     * @return
     */
    public String getParamValue(String paramName) {
        return paramValues.get(paramName);
    }

    public Object getRawRequestObject() {
        return rawRequestObject;
    }

    public void setRawRequestObject(Object rawRequestObject) {
        this.rawRequestObject = rawRequestObject;
    }
}

