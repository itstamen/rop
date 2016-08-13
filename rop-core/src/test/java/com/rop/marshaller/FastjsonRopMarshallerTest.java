package com.rop.marshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FastjsonRopMarshallerTest {

	private FastjsonRopMarshaller marshaller = new FastjsonRopMarshaller();

	@Test
	public void testMarshaller() throws IOException {
		Foo foo = new Foo();
		foo.setB1(true);
		foo.setB2(true);
		foo.setI1(1);
		foo.setI2(1);
		Bar bar = new Bar();
		bar.setFoo(foo);
		marshaller.marshaller(bar, System.out);
		List<Foo> lists = new ArrayList<Foo>(2);
		Foo f1 = new Foo();
		f1.setI2(2);
		lists.add(f1);
		Foo f2 = new Foo();
		f2.setI2(1);
		lists.add(f2);
		System.out.println();
		marshaller.marshaller(lists, System.out);
		System.out.println();

		Map<String, Foo> maps = new LinkedHashMap<String, Foo>(2);
		maps.put("1", f2);
		maps.put("2", f1);
		marshaller.marshaller(maps, System.out);
	}

}
