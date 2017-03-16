/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-4-17
 */
package com.rop.request;

import java.io.StringReader;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.rop.MessageFormat;
import com.rop.RopException;
import com.rop.RopRequestParseException;
import com.rop.impl.SimpleRopRequestContext;

/**
 * <pre>
 * 将参数中的XML或JSON转换为对象的属性对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopRequestMessageConverter implements ConditionalGenericConverter {

	private static final ConcurrentMap<Class, JAXBContext> jaxbContexts = new ConcurrentHashMap<Class, JAXBContext>();

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		/*
		 * AnnotationIntrospector introspector = new
		 * JaxbAnnotationIntrospector(); SerializationConfig serializationConfig
		 * = objectMapper.getSerializationConfig(); serializationConfig =
		 * serializationConfig.without(SerializationConfig.Feature.
		 * WRAP_ROOT_VALUE) .withAnnotationIntrospector(introspector);
		 * objectMapper.setSerializationConfig(serializationConfig);
		 */
		// libin:升级jackson到2.6.8
		SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(serializationConfig.getTypeFactory());
		objectMapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setAnnotationIntrospector(introspector);
	}

	/**
	 * 如果目标属性类有标注JAXB的注解，则使用该转换器
	 *
	 * @param sourceType
	 * @param targetType
	 * @return
	 */
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		Class clazz = targetType.getObjectType();
		return clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class);
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, Object.class));
	}

	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			if (SimpleRopRequestContext.messageFormat.get() == MessageFormat.json) {// 输入格式为JSON
				JsonParser jsonParser = objectMapper.getJsonFactory().createJsonParser((String) source);
				return jsonParser.readValueAs(targetType.getObjectType());
			} else {
				Unmarshaller unmarshaller = createUnmarshaller(targetType.getObjectType());
				StringReader reader = new StringReader((String) source);
				return unmarshaller.unmarshal(reader);
			}
		} catch (Exception e) {
			throw new RopRequestParseException((String) source, e);
		}
	}

	private Unmarshaller createUnmarshaller(Class clazz) throws JAXBException {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			return jaxbContext.createUnmarshaller();
		} catch (JAXBException ex) {
			throw new RopException("Could not create Unmarshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
		}
	}

	private JAXBContext getJaxbContext(Class clazz) {
		Assert.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = jaxbContexts.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz);
				jaxbContexts.putIfAbsent(clazz, jaxbContext);
			} catch (JAXBException ex) {
				throw new HttpMessageConversionException(
						"Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}

}
