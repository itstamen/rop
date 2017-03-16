/**
 *
 * 日    期：12-2-27
 */
package com.rop.marshaller;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.rop.RopException;
import com.rop.RopMarshaller;

/**
 * <pre>
 *    将响应对象流化成JSON。 {@link ObjectMapper}是线程安全的。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JacksonJsonRopMarshaller implements RopMarshaller {

	private static ObjectMapper objectMapper;

	public void marshaller(Object object, OutputStream outputStream) {
		try {
			// libin:升级jackson到2.6.8
			/*
			 * JsonGenerator jsonGenerator =
			 * getObjectMapper().getJsonFactory().createJsonGenerator(
			 * outputStream, JsonEncoding.UTF8);
			 */
			JsonGenerator jsonGenerator = getObjectMapper().getFactory().createGenerator(outputStream,
					JsonEncoding.UTF8);
			getObjectMapper().writeValue(jsonGenerator, object);
		} catch (IOException e) {
			throw new RopException(e);
		}
	}

	private ObjectMapper getObjectMapper() throws IOException {
		if (this.objectMapper == null) {
			ObjectMapper objectMapper = new ObjectMapper();
			// libin:升级jackson到2.6.8
			/*
			 * 
			 * AnnotationIntrospector introspector = new
			 * JaxbAnnotationIntrospector(); SerializationConfig
			 * serializationConfig = objectMapper.getSerializationConfig();
			 * serializationConfig =
			 * serializationConfig.without(SerializationConfig.Feature.
			 * WRAP_ROOT_VALUE) .with(SerializationConfig.Feature.INDENT_OUTPUT)
			 * .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
			 * .withSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY)
			 * .withAnnotationIntrospector(introspector);
			 * objectMapper.setSerializationConfig(serializationConfig);
			 */
			// libin:升级jackson到2.6.8
			SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
			AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(serializationConfig.getTypeFactory());
			objectMapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			objectMapper.setAnnotationIntrospector(introspector);
			this.objectMapper = objectMapper;
		}
		return this.objectMapper;
	}
}
