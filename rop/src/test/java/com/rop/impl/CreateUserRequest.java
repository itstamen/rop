/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-2-29
 */
package com.rop.impl;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class CreateUserRequest {

    @Pattern(regexp = "\\w{4,30}")
    private String userName;

    @Valid
    private Addresss address;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Addresss getAddress() {
        return address;
    }

    public void setAddress(Addresss address) {
        this.address = address;
    }
}

