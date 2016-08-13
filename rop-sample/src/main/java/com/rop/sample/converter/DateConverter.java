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
package com.rop.sample.converter;

import com.rop.converter.RopConverter;

import java.util.Date;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-18
 */
public class DateConverter implements RopConverter<String,Date> {


    public Date convert(String s) {
        return DateUtils.parseDate(s);
    }


    public String unconvert(Date date) {
        return DateUtils.format(date,DateUtils.DATETIME_FORMAT);
    }


    public Class<String> getSourceClass() {
        return String.class;
    }


    public Class<Date> getTargetClass() {
        return Date.class;
    }
}
