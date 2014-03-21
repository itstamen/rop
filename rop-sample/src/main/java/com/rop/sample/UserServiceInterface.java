/**
 * Copyright：中软海晟信息科技有限公司 版权所有 违者必究 2014 
 */
package com.rop.sample;

import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;
import com.rop.sample.request.LogonRequest;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-6
 */
public interface UserServiceInterface {

    @ServiceMethod(method = "user.getSession",version = "1.0",needInSession = NeedInSessionType.NO)
    Object getSession(LogonRequest request);
}
