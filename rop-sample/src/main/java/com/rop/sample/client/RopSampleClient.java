/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-8-4
 */
package com.rop.sample.client;

import com.rop.AbstractRopRequest;
import com.rop.RopRequest;
import com.rop.client.CompositeResponse;
import com.rop.client.DefaultRopClient;
import com.rop.sample.request.LogonRequest;
import com.rop.sample.request.TelephoneConverter;
import com.rop.sample.response.LogonResponse;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopSampleClient {

    public static final String SERVER_URL = "http://localhost:8088/router";

    private DefaultRopClient ropClient ;

    /**
     * 创建客户端对象
     * @param appKey
     * @param secret
     */
    public RopSampleClient(String appKey,String secret) {
        ropClient = new DefaultRopClient(SERVER_URL, appKey, secret);
        ropClient.setFormatParamName("messageFormat");
        ropClient.addRopConvertor(new TelephoneConverter());
    }

    /**
     * 登录系统
     *
     * @return
     */
    public String logon(String userName, String password) {
        LogonRequest ropRequest = new LogonRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setPassword("123456");
        CompositeResponse response = ropClient.get(ropRequest, LogonResponse.class, "user.logon", "1.0");
        String sessionId = ((LogonResponse) response.getSuccessResponse()).getSessionId();
        ropClient.setSessionId(sessionId);
        return sessionId;
    }

    public void logout() {
        RopRequest ropRequest = new AbstractRopRequest() {
        };
        ropClient.get(ropRequest, LogonResponse.class, "user.logout", "1.0");
    }

    public <T> CompositeResponse get(RopRequest ropRequest, Class<T> ropResponseClass,
                                     String methodName, String version) {
        return ropClient.get(ropRequest, ropResponseClass, methodName, version);
    }

    public <T> CompositeResponse post(RopRequest ropRequest, Class<T> ropResponseClass,
                                      String methodName, String version) {
        return ropClient.post(ropRequest, ropResponseClass, methodName, version);
    }
    
    public <T> CompositeResponse get(Map<String,Object> businessParams, Class<T> ropResponseClass,
                                     String methodName, String version) {
        return ropClient.get(businessParams, ropResponseClass, methodName, version);
    }

    public <T> CompositeResponse post(Map<String,Object> businessParams, Class<T> ropResponseClass,
                                      String methodName, String version) {
        return ropClient.post(businessParams, ropResponseClass, methodName, version);
    }
    public <T> CompositeResponse get(Map<String,Object> businessParams,List<String> ignoreSignParamNames, Class<T> ropResponseClass,
                                     String methodName, String version) {
        return ropClient.get(businessParams,ignoreSignParamNames, ropResponseClass, methodName, version);
    }

    public <T> CompositeResponse post(Map<String,Object> businessParams,List<String> ignoreSignParamNames, Class<T> ropResponseClass,
                                      String methodName, String version) {
        return ropClient.post(businessParams,ignoreSignParamNames, ropResponseClass, methodName, version);
    }
}

