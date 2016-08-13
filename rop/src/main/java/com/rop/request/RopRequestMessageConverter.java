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
package com.rop.request;

import com.rop.MessageFormat;
import com.rop.RopRequestParseException;
import com.rop.RopUnmarshaller;
import com.rop.impl.SimpleRopRequestContext;
import com.rop.unmarshaller.JacksonJsonRopUnmarshaller;
import com.rop.unmarshaller.JaxbXmlRopUnmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.Collections;
import java.util.Set;

/**
 * <pre>
 *     将参数中的XML或JSON转换为对象的属性对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopRequestMessageConverter implements ConditionalGenericConverter {

    private RopUnmarshaller xmlUnmarshaller;
    
    private RopUnmarshaller jsonUnmarshaller;
    
    /**
     * 如果目标属性类有标注JAXB的注解，则使用该转换器
     *
     * @param sourceType
     * @param targetType
     * @return
     */
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> clazz = targetType.getObjectType();
        return clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class);
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Object.class));
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            if (SimpleRopRequestContext.messageFormat.get() == MessageFormat.json) {//输入格式为JSON
            	return getJsonUnmarshaller().unmarshaller((String)source, targetType.getObjectType());
            } else {
                return getXmlUnmarshaller().unmarshaller((String)source, targetType.getObjectType());
            }
        } catch (Exception e) {
            throw new RopRequestParseException((String) source, e);
        }
    }

	public RopUnmarshaller getXmlUnmarshaller() {
		if(xmlUnmarshaller == null){
			xmlUnmarshaller = new JaxbXmlRopUnmarshaller();
		}
		return xmlUnmarshaller;
	}

	@Autowired(required=false)
	public void setXmlUnmarshaller(RopUnmarshaller xmlUnmarshaller) {
		this.xmlUnmarshaller = xmlUnmarshaller;
	}

	public RopUnmarshaller getJsonUnmarshaller() {
		if(jsonUnmarshaller == null){
			jsonUnmarshaller = new JacksonJsonRopUnmarshaller();
		}
		return jsonUnmarshaller;
	}

	@Autowired(required=false)
	public void setJsonUnmarshaller(RopUnmarshaller jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}
}

