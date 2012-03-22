/**
 *
 * 日    期：12-2-13
 */
package com.rop;

/**
 * <pre>
 *    安全控制管理器，决定用户是否有。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface SecurityManager {

    /**
     * 方法是否向用户开放
     *
     * @param method
     * @param userId
     * @return
     */
    boolean isGranted(RopServiceContext context);
}

