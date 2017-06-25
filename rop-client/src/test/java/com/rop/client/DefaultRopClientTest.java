/*
 * Copyright 2012-2017 the original author or authors.
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
package com.rop.client;

import org.junit.Test;

import com.rop.client.sign.DigestSignHandler;
import com.rop.response.CommonRopResponse;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopClientTest {

    private RopClient ropClient = new DefaultRopClient("http://localhost:8088/router", "00001");

    @Test
    public void testPostWithSession() throws Exception {
    	ropClient.setSignHandler(new DigestSignHandler("SHA-1", "abcdeabcdeabcdeabcdeabcde"));
    	ClientRequest request = ropClient.buildClientRequest();
    	CompositeResponse<CommonRopResponse> response = request.get(CommonRopResponse.class, "logout", "1.0");
    	response.isSuccessful();
    }
}

