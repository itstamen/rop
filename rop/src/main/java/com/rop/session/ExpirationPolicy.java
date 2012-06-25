package com.rop.session;

import java.io.Serializable;

/**
 * @author libinsong@gmail.com
 * @author 陈雄华
 */
public interface ExpirationPolicy extends Serializable {

    /**
     * 判断会话是否已经过期
     * @param sessionStatus
     * @return
     */
    boolean isExpired(Session session);
}
