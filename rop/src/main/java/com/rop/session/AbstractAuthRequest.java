package com.rop.session;

/**
 * @author : chenxh
 * @date: 13-8-13
 */
public abstract class AbstractAuthRequest implements AuthRequest {

    private Object detail;


    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

}
