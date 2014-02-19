package com.rop.session;

import java.io.Serializable;

/**
 * @author chenxh
 */
public interface Session extends Serializable {

    /**
     * 设置属性
     * @param name
     * @param obj
     */
    void setAttribute(String name, Object obj);

    /**
     * 获取属性
     * @param name
     * @return
     */
    Object getAttribute(String name);

}