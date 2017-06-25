/*
 * Copyright 2012-2017 the original author or authors.
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
package com.rop;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

/**
 * 支持的响应的格式类型
 */
public enum MessageFormat {
	
    XML, JSON, STREAM;

    public static MessageFormat getFormat(String value) {
        if (StringUtils.isBlank(value)) {
            return XML;
        } else {
            try {
                return MessageFormat.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
            	LoggerFactory.getLogger(MessageFormat.class).debug(e.getMessage(), e);
                return XML;
            }
        }
    }

    public static boolean isValidFormat(String value) {
        if (StringUtils.isBlank(value)) {
            return true;
        }else{
            try {
                MessageFormat.valueOf(value.toUpperCase());
                return true;
            } catch (IllegalArgumentException e) {
            	LoggerFactory.getLogger(MessageFormat.class).debug(e.getMessage(), e);
                return false;
            }
        }
    }

}
