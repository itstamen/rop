/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rop.sample.client;

import java.io.IOException;

import com.rop.client.ClientRequest;
import com.rop.client.CompositeResponse;
import com.rop.client.DefaultRopClient;
import com.rop.sample.request.LogonRequest;
import com.rop.sample.converter.TelephoneConverter;
import com.rop.sample.response.LogonResponse;

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
     * @throws IOException 
     */
    public String logon(String userName, String password) throws IOException {
        LogonRequest ropRequest = new LogonRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setPassword("123456");
        CompositeResponse<LogonResponse> response = ropClient.buildClientRequest().get(ropRequest, LogonResponse.class, "user.logon", "1.0");
        String sessionId = response.getSuccessResponse().getSessionId();
        ropClient.setSessionId(sessionId);
        return sessionId;
    }

    public void logout() throws IOException {
        ropClient.buildClientRequest().get(LogonResponse.class, "user.logout", "1.0");
    }

    public ClientRequest buildClientRequest(){
        return ropClient.buildClientRequest();
    }
}

