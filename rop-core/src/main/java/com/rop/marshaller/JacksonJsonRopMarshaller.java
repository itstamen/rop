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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rop.RopMarshaller;
import com.rop.utils.ObjectMapperUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <pre>
 *    将响应对象流化成JSON。 {@link ObjectMapper}是线程安全的。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JacksonJsonRopMarshaller implements RopMarshaller {

    private ObjectMapper objectMapper;

    public void marshaller(Object object, OutputStream outputStream) throws IOException {
    	JsonGenerator jsonGenerator = getObjectMapper().getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
    	getObjectMapper().writeValue(jsonGenerator, object);
    }

    public void setObjectMapper(ObjectMapper objectMapper){
    	this.objectMapper = objectMapper;
    }
    
	private ObjectMapper getObjectMapper() throws IOException {
    	if (objectMapper == null) {
    		objectMapper = ObjectMapperUtils.getObjectMapper();
        }
        return objectMapper;
    }
}

