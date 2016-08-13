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
package com.rop.sample;

import com.rop.MessageFormat;
import com.rop.client.ClientRequest;
import com.rop.client.CompositeResponse;
import com.rop.client.DefaultRopClient;
import com.rop.converter.UploadFile;
import com.rop.response.ErrorResponse;
import com.rop.response.MainErrorType;
import com.rop.sample.converter.TelephoneConverter;
import com.rop.sample.request.*;
import com.rop.sample.response.CreateUserResponse;
import com.rop.sample.response.LogonResponse;
import com.rop.sample.response.UploadUserPhotoResponse;
import com.rop.sample.response.UserListResponse;

import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class UserServiceClient {

    public static final String SERVER_URL = "http://localhost:8088/router";
    public static final String APP_KEY = "00001";
    public static final String APP_SECRET = "abcdeabcdeabcdeabcdeabcde";
    private DefaultRopClient ropClient = new DefaultRopClient(SERVER_URL, APP_KEY, APP_SECRET);

    {
        ropClient.setFormatParamName("messageFormat");
        ropClient.addRopConvertor(new TelephoneConverter());
    }


    @BeforeClass
    public void createSession() throws IOException {
        LogonRequest ropRequest = new LogonRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setPassword("123456");
        CompositeResponse<LogonResponse> response = ropClient.buildClientRequest()
                                   .get(ropRequest, LogonResponse.class, "user.getSession", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getSuccessResponse());
        assertTrue(response.getSuccessResponse() instanceof LogonResponse);
        assertEquals(((LogonResponse) response.getSuccessResponse()).getSessionId(), "mockSessionId1");
        ropClient.setSessionId(((LogonResponse) response.getSuccessResponse()).getSessionId());
    }

    @Test
    public void createSessionWithParamMap() throws IOException {
        CompositeResponse<LogonResponse> response = ropClient.buildClientRequest()
                .addParam("userName", "tomson")
                .addParam("password", "123456", true)
                .get(LogonResponse.class, "user.getSession", "1.0");

        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getSuccessResponse());
        assertTrue(response.getSuccessResponse() instanceof LogonResponse);
        assertEquals(((LogonResponse) response.getSuccessResponse()).getSessionId(), "mockSessionId1");
    }

    @Test
    public void addUser() throws IOException {
        CompositeResponse<LogonResponse> response = ropClient.buildClientRequest()
                .addParam("userName", "tomson")
                .addParam("password", "123456", true)
                .get(LogonResponse.class, "user.getSession", "1.0");
        String sessionId = response.getSuccessResponse().getSessionId();
        ropClient.setSessionId(sessionId);


        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUserName("katty");
        createUserRequest.setSalary(2500L);

        Telephone telephone = new Telephone();
        telephone.setZoneCode("010");
        telephone.setTelephoneCode("12345678");
        createUserRequest.setTelephone(telephone);

        //add1
        CompositeResponse<CreateUserResponse> createUserResponse = ropClient.buildClientRequest()
                .post(createUserRequest, CreateUserResponse.class, "user.add", "1.0");
        assertNotNull(createUserResponse);
        assertTrue(createUserResponse.isSuccessful());
        assertTrue(createUserResponse.getSuccessResponse() instanceof CreateUserResponse);

        //add2
        createUserResponse = ropClient.buildClientRequest()
                .post(createUserRequest, CreateUserResponse.class, "user.add", "1.0");
    }

    @Test
    public void addUserUseParamMap() throws IOException {
        CompositeResponse<LogonResponse> response = ropClient.buildClientRequest()
                .addParam("userName", "tomson")
                .addParam("password", "123456", true)
                .get(LogonResponse.class, "user.getSession", "1.0");
        String sessionId = response.getSuccessResponse().getSessionId();
        ropClient.setSessionId(sessionId);

        ClientRequest cr2 = ropClient.buildClientRequest();
        cr2.addParam("userName", "katty");
        cr2.addParam("salary", 2500L);
        Telephone telephone = new Telephone();
        telephone.setZoneCode("010");
        telephone.setTelephoneCode("12345678");
        cr2.addParam("telephone", telephone);

        CompositeResponse<CreateUserResponse> createUserResponse = cr2.post(CreateUserResponse.class, "user.add", "1.0");
        assertNotNull(createUserResponse);
        assertTrue(createUserResponse.isSuccessful());
        assertTrue(createUserResponse.getSuccessResponse() instanceof CreateUserResponse);
    }


    @Test
    public void testAddUserByVersion3() throws IOException {
        CreateUserRequest ropRequest = new CreateUserRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setSalary(2500L);
        ropClient.setMessageFormat(MessageFormat.xml);

        CompositeResponse<CreateUserResponse> response = ropClient.buildClientRequest()
                .post(ropRequest, CreateUserResponse.class, "user.add", "3.0");
        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertNull(response.getSuccessResponse());
        assertNotNull(response.getErrorResponse());
        assertTrue(response.getErrorResponse() instanceof ErrorResponse);
        assertEquals(response.getErrorResponse().getCode(), MainErrorType.UNSUPPORTED_VERSION.value());
    }

    @Test
    public void testFileUpload() throws Throwable {
        UploadUserPhotoRequest request = new UploadUserPhotoRequest();
        ClassPathResource resource = new ClassPathResource("photo.png");
        UploadFile uploadFile = new UploadFile(resource.getFile());
        request.setPhoto(uploadFile);
        request.setUserId("1");
        ropClient.setMessageFormat(MessageFormat.xml);

        CompositeResponse<UploadUserPhotoResponse> response = ropClient.buildClientRequest()
                .post(request, UploadUserPhotoResponse.class, "user.upload.photo", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof UploadUserPhotoResponse);
        assertEquals(response.getSuccessResponse().getFileType(), "png");
        assertEquals(response.getSuccessResponse().getLength(), uploadFile.getContent().length);
    }

    @Test
    public void testServiceXmlRequestAttr() throws Throwable {
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("tomson");
        request.setLocked(true);
        request.setSalary(2500L);
        Address address = new Address();
        address.setZoneCode("0001");
        address.setDoorCode("002");
        Street street1 = new Street();
        street1.setName("street1");
        street1.setNo("001");
        Street street2 = new Street();
        street2.setName("street2");
        street2.setNo("002");
        ArrayList<Street> streets = new ArrayList<Street>();
        streets.add(street1);
        streets.add(street2);
        address.setStreets(streets);
        request.setAddress(address);

        ropClient.setMessageFormat(MessageFormat.xml);
        CompositeResponse<CreateUserResponse> response = ropClient.buildClientRequest()
                .post(request, CreateUserResponse.class, "user.add", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof CreateUserResponse);
    }

    @Test
    public void testServiceJsonRequestAttr() throws Throwable {
        ropClient.setMessageFormat(MessageFormat.json);
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("tomson");
        request.setSalary(2500L);
        Address address = new Address();
        address.setZoneCode("0001");
        address.setDoorCode("002");
        Street street1 = new Street();
        street1.setName("street1");
        street1.setNo("001");
        Street street2 = new Street();
        street2.setName("street2");
        street2.setNo("002");
        ArrayList<Street> streets = new ArrayList<Street>();
        streets.add(street1);
        streets.add(street2);
        address.setStreets(streets);
        request.setAddress(address);

        CompositeResponse<CreateUserResponse> response = ropClient.buildClientRequest()
                .post(request, CreateUserResponse.class, "user.add", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof CreateUserResponse);
    }

    @Test
    public void testUserList() throws Throwable {
        ropClient.setMessageFormat(MessageFormat.json);
        CompositeResponse<UserListResponse> response = ropClient.buildClientRequest().get(UserListResponse.class,"user.list", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof UserListResponse);
    }


    @Test
    public void testCustomConverter() throws IOException {
        ropClient.addRopConvertor(new TelephoneConverter());
        CreateUserRequest request = new CreateUserRequest();
        request.setUserName("tomson");
        request.setSalary(2500L);
        Telephone telephone = new Telephone();
        telephone.setZoneCode("0592");
        telephone.setTelephoneCode("12345678");

        CompositeResponse<CreateUserResponse> response = ropClient.buildClientRequest().post(request, CreateUserResponse.class, "user.add", "1.0");

        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof CreateUserResponse);
    }
}

