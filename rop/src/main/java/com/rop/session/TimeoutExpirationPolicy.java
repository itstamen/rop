package com.rop.session;

import com.rop.session.ExpirationPolicy;
import com.rop.session.Session;
import com.rop.session.SessionStatus;

/**
 * @author libinsong@gmail.com
 * @author 陈雄华
 */
public final class TimeoutExpirationPolicy implements ExpirationPolicy {

    /**
     * 最大过期时间，单位为秒
     */
    private final long maxIdleInSeconds;

    /**
     * 基于最大会话闲置时间的过期策略
     * @param maxIdleInSeconds
     */
    public TimeoutExpirationPolicy(final long maxIdleInSeconds) {
        this.maxIdleInSeconds = maxIdleInSeconds;
    }

    @Override
    public boolean isExpired(Session session) {
        return session == null || session.getSessionStatus() == null ||
               (System.currentTimeMillis() - session.getSessionStatus().getLastAccessTime() >= this.maxIdleInSeconds);
    }
}
