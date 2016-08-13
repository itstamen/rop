package com.rop.unmarshaller;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.rop.marshaller.Bar;
import com.rop.marshaller.FastjsonRopMarshaller;
import com.rop.marshaller.Foo;
import com.rop.marshaller.JacksonJsonRopMarshaller;

public class JacksonJsonRopUnmarshallerTest {

	private FastjsonRopMarshaller fastjsonMarshaller = new FastjsonRopMarshaller();
	
	private JacksonJsonRopMarshaller jacksonJsonMarshaller = new JacksonJsonRopMarshaller();
	
	private JacksonJsonRopUnmarshaller unmarshaller = new JacksonJsonRopUnmarshaller();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUnmarshaller() throws IOException {
		Foo foo = new Foo();
		foo.setB1(true);
		foo.setB2(true);
		foo.setI1(1);
		foo.setI2(1);
		Bar bar = new Bar();
		bar.setFoo(foo);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		fastjsonMarshaller.marshaller(bar, baos);
		String json = baos.toString();
		Bar bar2 = unmarshaller.unmarshaller(json, Bar.class);
		assertNotNull(bar2);
		baos.reset();
		jacksonJsonMarshaller.marshaller(bar, baos);
		json = baos.toString();
		bar2 = unmarshaller.unmarshaller(json, Bar.class);
		assertNotNull(bar2);
		List<Foo> lists = new ArrayList<Foo>(2);
		Foo f1 = new Foo();
		f1.setI2(2);
		lists.add(f1);
		Foo f2 = new Foo();
		f2.setI2(1);
		lists.add(f2);
		baos.reset();
		fastjsonMarshaller.marshaller(lists, baos);
		json = baos.toString();
		List<Foo> list = unmarshaller.unmarshaller(json, lists.getClass());
		assertNotNull(list);
		baos.reset();
		jacksonJsonMarshaller.marshaller(lists, baos);
		json = baos.toString();
		list = unmarshaller.unmarshaller(json, lists.getClass());
		assertNotNull(list);

		Map<String, Foo> maps = new LinkedHashMap<String, Foo>(2);
		maps.put("1", f2);
		maps.put("2", f1);
		baos.reset();
		fastjsonMarshaller.marshaller(maps, baos);
		json = baos.toString();
		Map<String, Foo> map = unmarshaller.unmarshaller(json, maps.getClass());
		assertNotNull(map);
		baos.reset();
		jacksonJsonMarshaller.marshaller(maps, baos);
		json = baos.toString();
		map = unmarshaller.unmarshaller(json, maps.getClass());
		assertNotNull(map);
	}
}
