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
package com.rop;

import org.apache.commons.lang.StringUtils;

/**
 * 支持的响应的格式类型
 */
public enum MessageFormat {

    xml, json, stream;

    public static MessageFormat getFormat(String value) {
        if (StringUtils.isBlank(value)) {
            return xml;
        } else {
            try {
                return MessageFormat.valueOf(value.toLowerCase());
            } catch (IllegalArgumentException e) {
                return xml;
            }
        }
    }

    public static boolean isValidFormat(String value) {
        if (StringUtils.isBlank(value)) {
            return true;
        }else{
            try {
                MessageFormat.valueOf(value.toLowerCase());
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

}
