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

import com.rop.client.CompositeResponse;
import com.rop.sample.request.CreateUserRequest;
import com.rop.sample.request.Telephone;
import com.rop.sample.response.CreateUserResponse;

import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopSampleClientIT {
    public static final String APP_KEY = "00001";
    public static final String APP_SECRET = "abcdeabcdeabcdeabcdeabcde";

    RopSampleClient ropSampleClient = new RopSampleClient(APP_KEY,APP_SECRET);

    @Test
    public void testLogon() throws Exception {
        String sessionId = ropSampleClient.logon("tomson", "123456");
        assertNotNull(sessionId);
    }

    @Test
    public void testLogout() throws Exception {
        ropSampleClient.logout();
    }

    @Test
    public void addUser() throws IOException {

        ropSampleClient.logon("tomson", "123456");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUserName("katty");
        createUserRequest.setSalary(2500L);

        Telephone telephone = new Telephone();
        telephone.setZoneCode("010");
        telephone.setTelephoneCode("12345678");
        createUserRequest.setTelephone(telephone);

        CompositeResponse<CreateUserResponse> response = ropSampleClient.buildClientRequest()
                                                      .post(createUserRequest, CreateUserResponse.class, "user.add", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof CreateUserResponse);
    }
}

