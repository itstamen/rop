package com.rop.session;

/**
 * 认证请求
 * @author : chenxh
 * @date: 13-8-12
 */
public interface AuthRequest{

    /**
     * 认证的主体，对于常见的用户名/密码的认证请求来说，认证主体即是用户名。
     *
     * @return
     */
    Object getPrincipal();

    /**
     * 认证的凭据，对于常见的用户名/密码的认证请求来说，凭据即是密码。
     *
     * @return
     */
    Object getCredential();

    /**
     * 认证主体的其它信息，如IP地址，所属单位等信息
     *
     * @return
     */
    Object getDetail();
}
