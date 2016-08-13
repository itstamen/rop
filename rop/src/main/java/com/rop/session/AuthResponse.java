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
package com.rop.session;

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
