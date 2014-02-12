/**
 * Copyright： 版权所有 违者必究 2013
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

import java.util.UUID;

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

    private  String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().toUpperCase();
    }

}
