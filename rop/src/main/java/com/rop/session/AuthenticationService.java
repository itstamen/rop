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
 * 其中T是登录请求的类,而R是注销请求的类
 * @author : chenxh
 * @date: 13-10-16
 */

import com.rop.RopRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AuthenticationService<T extends RopRequest,R extends RopRequest> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 获取应用对应的认证管理器
     * @param appkey
     * @return
     */
    public abstract AuthenticationManager getAuthenticationManager(String appkey);

    /**
     * 该方法在子类在实现,并打上@ServiceMethod注解,作为登录的服务方法
     * @param loginRequest
     * @return
     */
    public abstract  Object logon(T logonRequest);


    /**
     * 该方法在子类在实现,并打上@ServiceMethod注解,作为注销的服务方法
     * @param loginRequest
     * @return
     */
    public abstract Object logout(R logoutRequest);

    /**
     * 对登录请求进行认证,在子类的{@link #logon(com.rop.RopRequest)}调用.
     * @param request
     * @return
     */
    protected AuthResponse  authentication(T request) {
        String appKey = request.getRopRequestContext().getAppKey();
        AuthenticationManager authenticationManager = getAuthenticationManager(appKey);
        AuthRequest authRequest = toAuthRequest(request);
        return authenticationManager.authenticate(authRequest);
    }


    /**
     * 将{@code logonRequest}转换为{@link AuthRequest}
     * @param logonRequest
     * @return
     */
    protected abstract AuthRequest toAuthRequest(Object logonRequest);

    /**
     * 该方法在子类的{@link #logout(com.rop.RopRequest)}方法中调用.
     * @param logoutRequest
     * @return
     */
    protected  void removeSession(R logoutRequest){
        logoutRequest.getRopRequestContext().removeSession();
    }
}
