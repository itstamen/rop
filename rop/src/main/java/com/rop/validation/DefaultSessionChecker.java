/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-3-1
 */
package com.rop.validation;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultSessionChecker implements SessionChecker {

    /**
     * 假设都是合法的会话，开发者可以根据应用实际情况进行会话的合法性判断
     * @param sessionId
     * @return
     */
    public boolean isValid(String sessionId) {
        return true;
    }
}

