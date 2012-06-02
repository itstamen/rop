/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-29
 */
package com.rop.marshaller;

import com.rop.MessageFormat;
import com.rop.RopRequest;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
public class MessageMarshallerUtilsTest {

    @Test
    public void testMarshallerRopRequest() throws Exception {
        RopRequest ropRequest = mock(RopRequest.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "key1Value");
        map.put("key2", "key2Value");
        map.put("key3", "key3Value");
        when(ropRequest.getParamValues()).thenReturn(map);
        String message = MessageMarshallerUtils.getMessage(ropRequest, MessageFormat.json);
        assertNotNull(message);
        assertTrue(message.indexOf("}") > -1);
        assertTrue(message.indexOf("{") > -1);
        assertTrue(message.indexOf("key1") > -1);
        assertTrue(message.indexOf("key1Value") > -1);

        message = MessageMarshallerUtils.getMessage(ropRequest, MessageFormat.xml);
        assertNotNull(message);
        assertTrue(message.indexOf("<") > -1);
        assertTrue(message.indexOf(">") > -1);
        assertTrue(message.indexOf("key1") > -1);
        assertTrue(message.indexOf("key1Value") > -1);
        System.out.println(message);
    }

    @Test
    public void testMarshallerRopResponse() throws Exception {
        SampleResponse response = new SampleResponse();
        response.setUserId("tom");
        response.setCreateTime("20120101");
        String message = MessageMarshallerUtils.getMessage(response, MessageFormat.json);
        assertTrue(message.indexOf("}") > -1);
        assertTrue(message.indexOf("{") > -1);
        assertTrue(message.indexOf("tom") > -1);
        assertTrue(message.indexOf("20120101") > -1);

        System.out.println(message);
        message = MessageMarshallerUtils.getMessage(response, MessageFormat.xml);
        assertTrue(message.indexOf("<?xml") > -1);
        assertTrue(message.indexOf(">") > -1);
        assertTrue(message.indexOf("tom") > -1);
        assertTrue(message.indexOf("20120101") > -1);
        System.out.println(message);
    }


}

