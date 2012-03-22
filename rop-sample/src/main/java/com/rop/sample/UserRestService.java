/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-2-29
 */
package com.rop.sample;

import com.rop.ApiMethod;
import com.rop.RopResponse;
import com.rop.response.ServiceErrorResponse;
import com.rop.sample.request.CreateUserRequest;
import com.rop.sample.response.CreateUserResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    private static final String USER_NAME_RESERVED = "USER_NAME_RESERVED";
    private List reservesUserNames = Arrays.asList(new String[]{"tom","jhon"});
    
    @ApiMethod("user.add")//② Let this method service the sample.user.add method
    public RopResponse addUser(CreateUserRequest request) {
        if(reservesUserNames.contains(request.getUserName())){ //如果注册的用户是预留的帐号，则返回错误的报文
            return new ServiceErrorResponse(
                    request.getMethod(), USER_NAME_RESERVED,request.getLocale(),request.getUserName());
        }else{
            CreateUserResponse response = new CreateUserResponse();
            //add creaet new user here...
            response.setCreateTime("20120101010101");
            response.setUserId("1");
            return response;
        }
    }
}

