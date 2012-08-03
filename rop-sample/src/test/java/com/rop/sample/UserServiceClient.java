/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-30
 */
package com.rop.sample;

import com.rop.MessageFormat;
import com.rop.client.CompositeResponse;
import com.rop.client.DefaultRopClient;
import com.rop.client.RopClient;
import com.rop.request.UploadFile;
import com.rop.response.ErrorResponse;
import com.rop.sample.request.*;
import com.rop.sample.response.CreateUserResponse;
import com.rop.sample.response.LogonResponse;
import com.rop.sample.response.UploadUserPhotoResponse;
import com.rop.security.FileUploadController;
import com.rop.security.MainErrorType;
import com.rop.utils.RopUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

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
    }


    @BeforeMethod
    public void createSession() {
        LogonRequest ropRequest = new LogonRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setPassword("123456");
        CompositeResponse response = ropClient.get(ropRequest, LogonResponse.class, "user.getSession", "1.0");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.getSuccessResponse());
        assertTrue(response.getSuccessResponse() instanceof LogonResponse);
        assertEquals(((LogonResponse) response.getSuccessResponse()).getSessionId(), "mockSessionId1");
    }


    /**
     * 在一切正确的情况下，返回正确的服务报文 (user.add + 3.0）
     */
    @Test
    public void testAddUserByVersion3() {
        CreateUserRequest ropRequest = new CreateUserRequest();
        ropRequest.setUserName("tomson");
        ropRequest.setSalary(2500L);
        ropClient.setMessageFormat(MessageFormat.xml);
        CompositeResponse response = ropClient.post(ropRequest, CreateUserResponse.class,
                "user.add", "3.0", "mockSessionId1");
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
        CompositeResponse response = ropClient.post(request, UploadUserPhotoResponse.class,
                "user.upload.photo", "1.0", "mockSessionId1");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof UploadUserPhotoResponse);
        assertEquals(((UploadUserPhotoResponse) response.getSuccessResponse()).getFileType(), "png");
        assertEquals(((UploadUserPhotoResponse) response.getSuccessResponse()).getLength(), uploadFile.getContent().length);
    }

    @Test
    public void testServiceXmlRequestAttr() throws Throwable {
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

        ropClient.setMessageFormat(MessageFormat.xml);
        CompositeResponse response = ropClient.post(request, CreateUserResponse.class,
                "user.add", "1.0", "mockSessionId1");
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

        CompositeResponse response = ropClient.post(request, CreateUserResponse.class,
                "user.add", "1.0", "mockSessionId1");
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.getSuccessResponse() instanceof CreateUserResponse);
    }
}

