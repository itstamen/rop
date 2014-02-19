package com.rop.session;

/**
 * @author : chenxh
 * @date: 13-8-13
 */
public abstract class AbstractAuthRequest implements AuthRequest {

    private Object detail;

    @Override
    public Object getDetail() {
        return null;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }

}
