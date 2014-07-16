/**
 *
 * 日    期：12-2-13
 */
package com.rop.impl;

import com.rop.security.ServiceAccessController;
import com.rop.session.Session;

/**
 * <pre>
 * 功能说明：对调用的方法进行安全性检查
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultServiceAccessController implements ServiceAccessController {


    public boolean isAppGranted(String appKey, String method, String version) {
        return true;
    }


    public boolean isUserGranted(Session session, String method, String version) {
        return true;
    }
}

