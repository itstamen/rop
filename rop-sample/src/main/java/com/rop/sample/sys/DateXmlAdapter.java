
package com.rop.sample.sys;

import com.rop.sample.converter.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-18
 */
public class DateXmlAdapter extends XmlAdapter<String,Date> {


    public Date unmarshal(String v) throws Exception {
        return DateUtils.parseDate(v);
    }


    public String marshal(Date date) throws Exception {
        return DateUtils.format(date,DateUtils.DATETIME_FORMAT);
    }
}
