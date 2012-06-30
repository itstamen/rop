package com.rop.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author libinsong@gmail.com
 * @author 陈雄华 itstamen@qq.com
 */
public final class DefaultSessionManager implements SessionManager {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Session> sessionCache = new ConcurrentHashMap<String, Session>(128, 0.75f, 32);

    @Override
    public void addSession(String sessionId, Session session) {
        sessionCache.put(sessionId, session);
    }

    @Override
    public Session getSession(String sessionId) {
        return sessionCache.get(sessionId);
    }

    @Override
    public void removeSession(String sessionId) {
        sessionCache.remove(sessionId);
    }

}