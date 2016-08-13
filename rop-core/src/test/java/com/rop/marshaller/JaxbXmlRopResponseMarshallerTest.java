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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

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

