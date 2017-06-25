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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rop.RopException;
import com.rop.RopUnmarshaller;
import com.rop.utils.ObjectMapperUtils;

import java.io.IOException;

/**
 * <pre>
 * 功能说明：使用jackson2.x将json数据转换成java对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class JacksonJsonRopUnmarshaller implements RopUnmarshaller {

    private ObjectMapper objectMapper;

    public <T> T unmarshaller(String content, Class<T> objectType) {
        try {
            return getObjectMapper().readValue(content, objectType);
        } catch (IOException e) {
            throw new RopException(e);
        }
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = ObjectMapperUtils.getObjectMapper();
        }
        return objectMapper;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper){
    	this.objectMapper = objectMapper;
    }
}

