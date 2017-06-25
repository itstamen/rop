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
package com.rop.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class ObjectMapperUtils {

	private static ObjectMapper objectMapper;
	
	public static ObjectMapper getObjectMapper(){
		if(objectMapper == null){
			synchronized (ObjectMapperUtils.class) {
				if(objectMapper == null){
					objectMapper = createObjectMapper();
				}
			}
		}
		return objectMapper;
	}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance(), true));
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:sss"));
		objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 空值处理为空串
		objectMapper.getSerializerProvider().setNullValueSerializer(
				new JsonSerializer<Object>() {
					@Override
					public void serialize(Object value, JsonGenerator jgen,
							SerializerProvider provider) throws IOException,
							JsonProcessingException {
						jgen.writeString("");
					}
				});
		// 进行HTML解码。
		objectMapper.registerModule(new SimpleModule().addSerializer(
				String.class, new JsonSerializer<String>() {
					@Override
					public void serialize(String value, JsonGenerator jgen,
							SerializerProvider provider) throws IOException,
							JsonProcessingException {
						jgen.writeString(StringEscapeUtils.unescapeHtml(value));
					}
				}));
		// 设置时区
		objectMapper.setTimeZone(TimeZone.getDefault());
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		ObjectMapperUtils.objectMapper = objectMapper;
	}
}
