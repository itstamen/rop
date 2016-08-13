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
package com.rop.marshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-27
 */
public class JacksonJsonRopMarshallerTest {

    private JacksonJsonRopMarshaller marshaller = new JacksonJsonRopMarshaller();

    @Test
    public void test1() throws IOException{
        Foo foo = new Foo();
        foo.setB1(true);
        foo.setB2(true);
        foo.setI1(1);
        foo.setI2(1);
        Bar bar = new Bar();
        bar.setFoo(foo);
        marshaller.marshaller(bar,System.out);
    }

    @Test
    public void test2() throws IOException{
        List<Foo> lists = new ArrayList<Foo>(2);
        Foo f1 = new Foo();
        f1.setI2(2);
        lists.add(f1);
        Foo f2 = new Foo();
        f2.setI2(1);
        lists.add(f2);
        System.out.println();
        marshaller.marshaller(lists,System.out);
        System.out.println();

        Map<String,Foo> maps= new LinkedHashMap<String,Foo>(2);
        maps.put("1",f2);
        maps.put("2",f1);
        marshaller.marshaller(maps,System.out);
    }
}
