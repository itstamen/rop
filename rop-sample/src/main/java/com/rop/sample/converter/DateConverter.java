/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-8
 */
package com.rop.sample.converter;

import com.rop.request.RopConverter;

import java.util.Date;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-18
 */
public class DateConverter implements RopConverter<String,Date> {

    @Override
    public Date convert(String s) {
        return DateUtils.parseDate(s);
    }

    @Override
    public String unconvert(Date date) {
        return DateUtils.format(date,DateUtils.DATETIME_FORMAT);
    }

    @Override
    public Class<String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<Date> getTargetClass() {
        return Date.class;
    }
}
