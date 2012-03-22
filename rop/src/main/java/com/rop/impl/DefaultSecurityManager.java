/**
 *
 * 日    期：12-2-13
 */
package com.rop.impl;

import com.rop.*;

/**
 * <pre>
 * 功能说明：对调用的方法进行安全性检查
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultSecurityManager implements com.rop.SecurityManager {

    public boolean isGranted(RopServiceContext context) {
        return true;
    }

}

