/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-18
 */
package com.rop.marshaller;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopResponseMarshallerTest {

    private JaxbXmlRopMarshaller marshaller = new JaxbXmlRopMarshaller();

    @Test
    public void buildMarshaller() throws Throwable {
        //marshaller.marshaller(new SampleResponse(), new ByteArrayOutputStream(1024));
        SampleResponse sampleResponse = new SampleResponse();
        List<HashMap<String, Object>> table = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> row = new HashMap<String, Object>();
        row.put("c1", "c1");
        row.put("c2", "c2");
        row.put("c3", "c3");
        table.add(row);
        sampleResponse.setUserId("json");
        sampleResponse.setTable(table);
        marshaller.marshaller(sampleResponse, System.out);
    }

    @Test
    public void test1(){
        Foo foo = new Foo();
        foo.setB1(true);
        foo.setB2(true);
        foo.setI1(1);
        foo.setI2(1);
        marshaller.marshaller(foo,System.out);
    }
}

