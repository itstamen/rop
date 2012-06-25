/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-20
 */
package com.rop.session;

import org.springframework.util.Assert;

/**
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractSessionManager implements SessionManager{

    private ExpirationPolicy expirationPolicy;

    @Override
    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy = expirationPolicy;
    }

    /**
     * 检查会话状态，如果会话过期，将其标识为expired，并且移除会话
     * @param sessionId
     * @return
     */
    @Override
    public final Session getSession(String sessionId) {

        Session sessionFromCache = getSessionFromCache(sessionId);

        //已经设置了过期策略
        if(expirationPolicy !=null && sessionFromCache != null){
            sessionFromCache.setSessionStatus(getSessionStatus(sessionId));
            boolean expired = expirationPolicy.isExpired(sessionFromCache);
            if(expired){
                killSession(sessionId);
                sessionFromCache = null;
            }
        }
        return sessionFromCache;
    }

    /**
     * 从会话缓存中获取会话
     * @return
     */
    protected abstract Session getSessionFromCache(String sessionId);
}

