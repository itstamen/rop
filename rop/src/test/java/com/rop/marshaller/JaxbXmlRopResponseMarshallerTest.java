/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-18
 */
package com.rop.marshaller;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopResponseMarshallerTest {

    @Test
    public void buildMarshaller() throws Throwable {
        JaxbXmlRopMarshaller marshaller = new JaxbXmlRopMarshaller();
        marshaller.marshaller(new SampleResponse(), new ByteArrayOutputStream(1024));
    }
}

