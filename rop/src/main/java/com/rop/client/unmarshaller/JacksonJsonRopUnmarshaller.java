/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-30
 */
package com.rop.client.unmarshaller;

import java.io.IOException;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.rop.RopException;
import com.rop.client.RopUnmarshaller;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JacksonJsonRopUnmarshaller implements RopUnmarshaller {

	private static ObjectMapper objectMapper;

	public <T> T unmarshaller(String content, Class<T> objectType) {
		try {
			return getObjectMapper().readValue(content, objectType);
		} catch (IOException e) {
			throw new RopException(e);
		}
	}

	private ObjectMapper getObjectMapper() throws IOException {
		if (this.objectMapper == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			// libin:升级jackson到2.6.8
			/*
			 * AnnotationIntrospector introspector = new
			 * JaxbAnnotationIntrospector(); SerializationConfig
			 * serializationConfig = objectMapper.getSerializationConfig();
			 * serializationConfig =
			 * serializationConfig.without(SerializationConfig.Feature.
			 * WRAP_ROOT_VALUE) .withAnnotationIntrospector(introspector);
			 * objectMapper.setSerializationConfig(serializationConfig);
			 */
			// libin:升级jackson到2.6.8
			SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
			AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(serializationConfig.getTypeFactory());
			objectMapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
			objectMapper.setAnnotationIntrospector(introspector);
			this.objectMapper = objectMapper;
		}
		return this.objectMapper;
	}
}
