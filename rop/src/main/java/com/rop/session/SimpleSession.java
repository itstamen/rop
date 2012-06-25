/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-21
 */
package com.rop.session;

import com.rop.session.Session;
import com.rop.session.SessionStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SimpleSession implements Session {

    private String id;
    
    private Map<String,Object> attributes = new HashMap<String,Object>();

    private SessionStatus sessionStatus;

    public SimpleSession(String id) {
        this.id = id;
        this.sessionStatus = new SessionStatus(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    @Override
    public void setSessionStatus(SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    @Override
    public void setAttribute(String name, Object obj) {
        attributes.put(name,obj);
        sessionStatus.markModified();//更改了数据
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}

