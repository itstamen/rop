/**
 *
 * 日    期：12-2-27
 */
package com.rop.impl;

import com.rop.*;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SimpleRopServiceContext implements RopServiceContext {
    private String method;
    private Locale locale;
    private RopServiceHandler ropServiceHandler;
    private ResponseFormat responseFormat;
    private HttpServletRequest webRequest;
    private RopResponse ropResponse;
    private RopRequest ropRequest;
    private String sessionId;
    private List<ObjectError> allErrors;
    private String appKey;


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

    public HttpServletRequest getWebRequest() {
        return this.webRequest;
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

    public List<ObjectError> getAllErrors() {
        return allErrors;
    }

    public void setAllErrors(List<ObjectError> allErrors) {
        this.allErrors = allErrors;
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

    public void setWebRequest(HttpServletRequest webRequest) {
        this.webRequest = webRequest;
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
}

