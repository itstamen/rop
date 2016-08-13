package com.rop.unmarshaller;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.rop.marshaller.Foo;
import com.rop.marshaller.JaxbXmlRopMarshaller;
import com.rop.marshaller.SampleResponse;

public class JaxbXmlRopUnmarshallerTest {

	private JaxbXmlRopMarshaller marshaller = new JaxbXmlRopMarshaller();
	
	private JaxbXmlRopUnmarshaller unmarshaller = new JaxbXmlRopUnmarshaller();
	
	@Test
	public void testUnmarshaller() {
		Foo foo = new Foo();
		foo.setB1(true);
		foo.setB2(true);
		foo.setI1(1);
		foo.setI2(1);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshaller(foo, baos);
		String xml = baos.toString();
		Foo foo2 = unmarshaller.unmarshaller(xml, Foo.class);
		assertEquals(foo, foo2);
		SampleResponse sampleResponse = new SampleResponse();
        List<HashMap<String, Object>> table = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> row = new HashMap<String, Object>();
        row.put("c1", "c1");
        row.put("c2", "c2");
        row.put("c3", "c3");
        table.add(row);
        sampleResponse.setUserId("json");
        sampleResponse.setTable(table);
        baos.reset();
		marshaller.marshaller(sampleResponse, baos);
		xml = baos.toString();
		SampleResponse response = unmarshaller.unmarshaller(xml, SampleResponse.class);
		assertNotNull(response);
	}

}
