/**
 *
 * 日    期：12-2-27
 */
package com.rop.impl;

import com.rop.*;
import com.rop.validation.MainError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SimpleRopServiceContext implements RopServiceContext {

    public static final String HTTP_SERVLET_REQUEST_ATTRNAME = "$HTTP_SERVLET_REQUEST_ATTRNAME";

    public static final String SPRING_VALIDATE_ERROR_ATTRNAME = "$SPRING_VALIDATE_ERROR_ATTRNAME";

    private Map<String, Object> attributes = new HashMap<String, Object>();

    private String method;

    private Locale locale;

    private RopServiceHandler ropServiceHandler;

    private ResponseFormat responseFormat;

    private MainError mainError;

    private RopResponse ropResponse;

    private RopRequest ropRequest;

    private String sessionId;

    private String appKey;

    private boolean needCheckSign ;

    public String getMethod() {
        return this.method;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public RopServiceHandler getRopServiceHandler() {
        return this.ropServiceHandler;
    }

    public ResponseFormat getResponseFormat() {
        return this.responseFormat;
    }

    public RopResponse getRopResponse() {
        return this.ropResponse;
    }

    public RopRequest getRopRequest() {
        return this.ropRequest;
    }

    public void setRopRequest(RopRequest ropRequest) {
        this.ropRequest = ropRequest;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setRopServiceHandler(RopServiceHandler ropServiceHandler) {
        this.ropServiceHandler = ropServiceHandler;
    }

    public void setResponseFormat(ResponseFormat responseFormat) {
        this.responseFormat = responseFormat;
    }


    public void setRopResponse(RopResponse ropResponse) {
        this.ropResponse = ropResponse;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setMainError(MainError mainError) {
        this.mainError = mainError;
    }

    public MainError getMainError() {
        return this.mainError;
    }

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public boolean isNeedCheckSign() {
        return needCheckSign;
    }

    public void setNeedCheckSign(boolean needCheckSign) {
        this.needCheckSign = needCheckSign;
    }
}

