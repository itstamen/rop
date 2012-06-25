package com.rop.session;

import java.io.Serializable;

/**
 * @author libinsong@gmail.com
 */
public interface Session extends Serializable {

    /**
     * 会话的ID，即通过{@link com.rop.config.SystemParameterNames#getSessionId()}系统级参数传递过来的会话ID
     * @return
     */
    String getId();

    /**
     * 获取会话的状态
     * @return
     */
    SessionStatus getSessionStatus();

    /**
     * 设置会话状态
     * @param sessionStatus
     */
    void setSessionStatus(SessionStatus sessionStatus);

    /**
     * 设置会话的属性
     * @return
     */
    void setAttribute(String name,Object obj);

    /**
     * 获取会话的属性对象
     * @param name
     * @return
     */
    Object getAttribute(String name);
}