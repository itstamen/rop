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
package com.rop.unmarshaller;

import com.rop.RopException;
import com.rop.RopUnmarshaller;

import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 功能说明：使用Jaxb将xml数据转换成java对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JaxbXmlRopUnmarshaller implements RopUnmarshaller {

    private static Map<Class<?>, JAXBContext> jaxbContextHashMap = new ConcurrentHashMap<Class<?>, JAXBContext>();


    public <T> T unmarshaller(String content, Class<T> objectType) {
        try {
            Unmarshaller unmarshaller = buildUnmarshaller(objectType);
            StringReader reader = new StringReader(content);
            new InputSource(reader);
            return objectType.cast(unmarshaller.unmarshal(reader));
        } catch (JAXBException e) {
            throw new RopException(e);
        }
    }

    private Unmarshaller buildUnmarshaller(Class<?> objectType) throws JAXBException {
        if (!jaxbContextHashMap.containsKey(objectType)) {
            JAXBContext context = JAXBContext.newInstance(objectType);
            jaxbContextHashMap.put(objectType, context);
        }
        JAXBContext context = jaxbContextHashMap.get(objectType);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller;
    }
}

