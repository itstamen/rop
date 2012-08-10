/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-7-27
 */
package com.rop.sample;

import com.rop.RopRequest;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@ServiceMethodBean(version = "1.0")
public class LogoutService {

    @ServiceMethod(method = "user.logout")
    public Object logout(RopRequest request){
        return null;
    }
}

