package com.rop.session;

import java.util.HashMap;
import java.util.Map;

/**
 *认证结果，如果认证成功，则设置{@link Session}，否则设置{@link #errorCode},{@link #errorCode}必须
 * 在ROP的国际化文件中定义。
 * @author : chenxh
 * @date: 13-8-13
 */
public class AuthResponse {

    private Session ropSession;

    private boolean authenticated = false;

    private String errorCode;

    public Session getRopSession() {
        return ropSession;
    }

    public void setRopSession(Session logonSession) {
        this.ropSession = logonSession;
        this.authenticated = true;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
