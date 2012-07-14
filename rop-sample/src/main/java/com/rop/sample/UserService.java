/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-2-29
 */
package com.rop.sample;

import com.rop.RopRequest;
import com.rop.RopResponse;
import com.rop.annotation.HttpAction;
import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;
import com.rop.response.ServiceErrorResponse;
import com.rop.sample.request.CreateUserRequest;
import com.rop.sample.request.LogonRequest;
import com.rop.sample.response.CreateUserResponse;
import com.rop.sample.response.LogonResponse;
import com.rop.session.SimpleSession;

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
@ServiceMethodBean
public class UserService {

    private static final String USER_NAME_RESERVED = "USER_NAME_RESERVED";
    private List reservesUserNames = Arrays.asList(new String[]{"tom", "jhon"});

    @ServiceMethod(value = "user.getSession", version = "1.0", needInSession = NeedInSessionType.NO)
    public RopResponse getSession(LogonRequest request) {
        //创建一个会话
        SimpleSession session = new SimpleSession();
        session.setAttribute("userName",request.getUserName());
        request.getRequestContext().addSession("mockSessionId1", session);

        //返回响应
        LogonResponse logonResponse = new LogonResponse();
        logonResponse.setSessionId("mockSessionId1");
        return logonResponse;
    }

    @ServiceMethod(value = "user.add", version = "1.0")//② Let this method service the sample.user.add method
    public RopResponse addUser(CreateUserRequest request) {
        if (reservesUserNames.contains(request.getUserName())) { //如果注册的用户是预留的帐号，则返回错误的报文
            //这个业务错误将引用扩展国际化错误资源中的消息（i18n/rop/sampleRopError）
            return new ServiceErrorResponse(
                    request.getRequestContext().getMethod(), USER_NAME_RESERVED,
                    request.getRequestContext().getLocale(), request.getUserName());
        } else {
            CreateUserResponse response = new CreateUserResponse();
            //add creaet new user here...
            response.setCreateTime("20120101010101");
            response.setUserId("1");
            return response;
        }
    }

    //版本为2.0的user.add
    @ServiceMethod(value = "user.add", version = "2.0")
    public RopResponse addUser2(CreateUserRequest request) {
        if (reservesUserNames.contains(request.getUserName())) { //如果注册的用户是预留的帐号，则返回错误的报文
            return new ServiceErrorResponse(
                    request.getRequestContext().getMethod(), USER_NAME_RESERVED,
                    request.getRequestContext().getLocale(), request.getUserName());
        } else {
            CreateUserResponse response = new CreateUserResponse();
            //add creaet new user here...
            response.setCreateTime("20120101010102");
            response.setUserId("2");
            return response;
        }
    }

    //版本为4.0的user.add:不需要会话
    @ServiceMethod(value = "user.add", version = "4.0", needInSession = NeedInSessionType.NO)
    public RopResponse addUser4(CreateUserRequest request) {
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010102");
        response.setUserId("4");
        return response;
    }

    //模拟一个会过期的服务（过期时间为1秒）
    @ServiceMethod(value = "user.timeout", version = "1.0", timeout = 1)
    public RopResponse timeoutService(CreateUserRequest request) throws Throwable {
        Thread.sleep(2000);
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010102");
        response.setUserId("2");
        return response;
    }

    @ServiceMethod(value = "user.rawRopRequest", version = "1.0")
    public RopResponse useRawRopRequest(RopRequest request) throws Throwable {
        String userId = request.getRequestContext().getParamValue("userId");
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010102");
        response.setUserId(userId);
        return response;
    }

    @ServiceMethod(value = "user.customConverter", version = "1.0")
    public RopResponse customConverter(CreateUserRequest request) throws Throwable {
        String userId = request.getRequestContext().getParamValue("userId");
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010102");
        response.setUserId(userId);
        response.setFeedback(request.getTelephone().getZoneCode() + "#" + request.getTelephone().getTelephoneCode());
        return response;
    }

    //直接使用RopRequest对象作为入参
    @ServiceMethod(value = "user.query", version = "1.0", httpAction = HttpAction.GET)
    public RopResponse queryUsers(RopRequest request) throws Throwable {
        //直接从参数列表中获取参数值
        String userId = request.getRequestContext().getParamValue("userId");
        CreateUserResponse response = new CreateUserResponse();
        response.setCreateTime("20120101010102");
        response.setUserId(userId);
        response.setFeedback("user.query");
        return response;
    }

    @ServiceMethod(value = "user.get", version = "1.0", httpAction = HttpAction.GET)
    public RopResponse getUser(CreateUserRequest request) throws Throwable {
        String userId = request.getRequestContext().getParamValue("userId");
        CreateUserResponse response = new CreateUserResponse();
        //add creaet new user here...
        response.setCreateTime("20120101010102");
        response.setUserId(userId);
        response.setFeedback("user.get");
        return response;
    }


}

