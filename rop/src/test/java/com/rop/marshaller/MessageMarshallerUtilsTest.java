/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-29
 */
package com.rop.marshaller;

import com.beust.jcommander.internal.Maps;
import com.rop.MessageFormat;
import com.rop.RopRequest;
import com.rop.RopRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;

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

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testMarshallerRopRequest() throws Exception {
        RopRequest ropRequest = mock(RopRequest.class);
        RopRequestContext msc = mock(RopRequestContext.class);
        when(ropRequest.getRopRequestContext()).thenReturn(msc);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "key1Value");
        map.put("key2", "key2Value");
        map.put("key3", "key3Value");
        when(msc.getAllParams()).thenReturn(map);
        String message = MessageMarshallerUtils.getMessage(ropRequest, MessageFormat.json);
        assertNotNull(message);
        logger.info("json:{}",message);
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
        logger.info("xml:{}", message);
    }

    @Test
    public void testMarshallerRopResponse() throws Exception {
        SampleResponse response = new SampleResponse();
        response.setUserId("tom");
        response.setCreateTime("20120101");


        List<HashMap<String,Object>> table = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> row1 = new HashMap<String, Object>();
        row1.put("col1", "id1");
        row1.put("col2", "user1");
        row1.put("col3", 20);
        row1.put("col4", 1000.34);
        table.add(row1);
        HashMap<String,Object> row2 = new HashMap<String, Object>();
        row2.put("col1", "id2");
        row2.put("col2", null);
        row2.put("col3", 22);
        row2.put("col4", 2000.34);
        table.add(row2);
        response.setTable(table);

        HashMap<String,String> attaches = new HashMap<String, String>();
        attaches.put("a","aa");
        attaches.put("b","bb");
        attaches.put("c","cc");
        response.setAttaches(attaches);

        String message = MessageMarshallerUtils.getMessage(response, MessageFormat.json);
        assertTrue(message.indexOf("}") > -1);
        assertTrue(message.indexOf("{") > -1);
        assertTrue(message.indexOf("tom") > -1);
        assertTrue(message.indexOf("20120101") > -1);

        logger.info("json:{}",message);
    }
}

