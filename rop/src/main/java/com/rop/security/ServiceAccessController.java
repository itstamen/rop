/**
 *
 * 日    期：12-2-13
 */
package com.rop.security;

import com.rop.session.Session;

/**
 * <pre>
 *    安全控制控制器，决定用户是否有。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface ServiceAccessController {

    /**
     * 服务方法是否向ISV开放
     * @param method
     * @param userId
     * @return
     */
    boolean isAppGranted(String appKey, String method, String version);

    /**
     *  服务方法是否向当前用户开放
     * @param ropRequestContext
     * @return
     */
    boolean isUserGranted(Session session,String method,String version);
}

