package com.rop.session;

/**
 * @author : chenxh
 * @date: 13-8-12
 */
public interface AuthenticationManager {

    /**
     * 对{@link AuthRequest}进行认证，并产生{@link AuthResponse}。如果认证成功，需要生成{@link com.rop.session.Session}
     *
     * @param authRequest
     * @return
     */
    AuthResponse authenticate(AuthRequest authRequest);

    /**
     * 哪些应用使用这个认证管理器
     *
     * @return
     */
    String[] appkeys();

    /**
     * 是否是默认的认证器，如果某个应用没有对应的认证器，将采用默认的认证器
     *
     * @return
     */
    boolean isDefault();

}
