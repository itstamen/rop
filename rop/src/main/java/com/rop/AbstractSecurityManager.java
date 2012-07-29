/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-7-29
 */
package com.rop;

import com.rop.session.Session;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractSecurityManager implements com.rop.SecurityManager{

    @Override
    public boolean isIsvGranted(String appKey, String method, String version) {
        return true;
    }

    @Override
    public boolean isUserGranted(Session session, String method, String version) {
        return true;
    }
}

