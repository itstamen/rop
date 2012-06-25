/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-20
 */
package com.rop.session;

import java.io.Serializable;

/**
 * <pre>
 *   会话的状态
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SessionStatus implements Serializable {

    //统计周期
    private static final int STATISTIC_PERIOD = 60*1000;

    //会话的ID
    private String sessionId;

    //会话创建时间
    private long createTime;

    //至创建以来的访问次数
    private int accessCount;

    //上次访问时间
    private long lastAccessTime;

    //会话Object最后更改时间
    private long lastModifiedTime;

    //上次统计时间
    private long lastStatisticTime;

    //最近一段时间访问次数
    private int  lastAccessCount;

    public SessionStatus(String sessionId) {
        this.sessionId = sessionId;
        long currTime = System.currentTimeMillis();
        this.createTime = currTime;
        this.lastAccessTime = currTime;
        this.lastModifiedTime = currTime;
        this.lastStatisticTime =currTime;
        this.lastAccessCount = 1;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public int getLastAccessCount() {
        return lastAccessCount;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * 会话访问信息更新
     */
    public void markAccess(){
        long currTime = System.currentTimeMillis();
        lastAccessTime = currTime;
        accessCount++;
        if(currTime - lastStatisticTime > STATISTIC_PERIOD){
            lastStatisticTime = currTime;
            lastAccessCount = 1;
        }
    }

    /**
     * 表示会话的内容发生了变更，在群集环境下，可以根据{@link #lastModifiedTime}同步本节点的会话信息
     */
    public void markModified() {
        this.lastModifiedTime = System.currentTimeMillis();
    }
}

