/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-3-1
 */
package com.rop.validation;

/**
 * <pre>
 *     用户会话的感知接口
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface SessionChecker {

    /**
     * 是否是有效的会话
     * @param sessionId
     * @return
     */
    boolean isValid(String sessionId);
}

