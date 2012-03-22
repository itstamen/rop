/**
 *
 * 日    期：12-2-10
 */
package com.rop;

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
    private String v = "1.0";

    //会话ID
    private String sessionId;

    //应用键
    private String appKey;

    //签名
    @IgnoreSign
    private String sign;

    //格式
    private String format = "xml";

    //本地化
    private Locale locale = Locale.CHINESE;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
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

}

