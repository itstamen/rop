/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-2-29
 */
package com.sample.rop;

import com.rop.ApiMethod;
import com.sample.rop.request.CreateUserRequest;
import com.sample.rop.response.CreateUserResponse;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@Service
public class UserRestService {

    @ApiMethod("sample.user.add")//② Let this method service the sample.user.add method
    public CreateUserResponse addUser(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010101");
        response.setUserId("1");
        return response;
    }
}

