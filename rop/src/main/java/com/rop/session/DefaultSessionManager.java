package com.rop.session;

import com.rop.session.AbstractSessionManager;
import com.rop.session.Session;
import com.rop.session.SessionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  使用一个JVM内部的Map保存会话对象，它仅能正确工作于单JVM的环境，如果使用了集群，则需要使用
 *memcached或ehcache等全局缓存保存会话。
 * @author libinsong@gmail.com
 * @author 陈雄华 itstamen@qq.com
 */
public final class DefaultSessionManager extends AbstractSessionManager {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String,Session> sessionCache;

    private final Map<String,SessionStatus> sessionStatusCache;

    public DefaultSessionManager() {
        this.sessionCache = new ConcurrentHashMap<String, Session>();
        this.sessionStatusCache = new ConcurrentHashMap<String, SessionStatus>();
    }

    public DefaultSessionManager(int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this.sessionCache = new ConcurrentHashMap<String, Session>(initialCapacity, loadFactor, concurrencyLevel);
        this.sessionStatusCache =
                new ConcurrentHashMap<String, SessionStatus>(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    protected Session getSessionFromCache(String sessionId) {
        Session session = sessionCache.get(sessionId);
        if(session != null){
            session.setSessionStatus(sessionStatusCache.get(sessionId));
        }
        return session;
    }

    @Override
    public void addSession(Session session) {
        sessionCache.put(session.getId(),session);
        sessionStatusCache.put(session.getId(),session.getSessionStatus());
    }

    @Override
    public SessionStatus getSessionStatus(String sessionId) {
        Session session = sessionCache.get(sessionId);
        if(session != null){
            return null;
        }else{
            return session.getSessionStatus();
        }
    }

    @Override
    public void killSession(String sessionId) {
        sessionCache.remove(sessionId);
        sessionStatusCache.remove(sessionId);
    }
}