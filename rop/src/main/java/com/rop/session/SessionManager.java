package com.rop.session;

import java.util.Collection;

import com.rop.session.Session;

/**
 * 会话管理器
 * @author libinsong@gmail.com
 * @author 陈雄华
 */
public interface SessionManager {

    /**
     * 注册一个会话
     * @param session
     */
    void addSession(Session session);

    /**
     * 从注册表中获取会话
     * @param sessionId
     * @return
     */
    Session getSession(String sessionId);

    /**
     * 获取会话的状态信息
     * @param sessionId
     * @return
     */
    SessionStatus getSessionStatus(String sessionId);

    /**
     *  杀掉这个会话
     * @param sessionId
     * @return
     */
    void killSession(String sessionId);

    /**
     * 设置会话过期策略
     * @param expirationPolicy
     */
    void setExpirationPolicy(ExpirationPolicy expirationPolicy);
}

