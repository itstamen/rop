/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012
 * 日    期：12-7-27
 */
package com.rop.sample.converter;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间日期的工具类
 *
 * @author : chenxh(quickselect@163.com)
 * @date: 13-7-1
 */
public class DateUtils {

    public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATETIME_FORMAT);
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String SHOW_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SHOW_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 获取当前时间串，格式为：yyyymmddHHMiSS
     *
     * @return
     */
    public static final String getCurrDatetime() {
        return format(new Date(), DATETIME_FORMAT);
    }

    /**
     * 获取当前日期串，格式为yyyymmdd
     *
     * @return
     */
    public static final String getCurrDate() {
        return format(new Date(), DATE_FORMAT);
    }


    /**
     * @param date      时间
     * @param formatStr 格式化串
     * @return
     */
    public static String format(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        return sdf.format(date);
    }

    /**
     * 对{@code date}字符串，采用<code>0</code>右补齐到{@code length}的长度
     *
     * @param date
     * @param length
     * @return
     */
    public static String pad0(String date, int length) {
        return padChar(date, length, '0');
    }

    /**
     * 对{@code date}字符串，采用<code>9</code>右补齐到{@code length}的长度
     *
     * @param date
     * @param length
     * @return
     */
    public static String pad9(String date, int length) {
        return padChar(date, length, '9');
    }

    private static String padChar(String date, int length, char theChar) {
        if (StringUtils.isEmpty(date)) {
            date = "";
        }
        return StringUtils.rightPad(date, length, theChar);
    }

    /**
     * {@code time1}是否小于{@code time2},即类似于<pre>time1 < time2</pre>。 如果{@code time2}为<code>null</code>，
     * 则视为最小。
     *
     * @param time1 时间字符串，格式为 yyyyMMddHHmmss，不足14位后补0
     * @param time2 时间字符串，格式为 yyyyMMddHHmmss，不足14位后补0
     * @return
     */
    public static boolean lessThan(String time1, String time2) {
        if (StringUtils.isEmpty(time1)) {
            if (StringUtils.isEmpty(time2)) {
                return false;
            } else {
                return true;
            }
        } else {
            return time1.compareTo(time2) < 0;
        }
    }


    /**
     * {@code time1}是否大于{@code time2},即类似于<pre>time1 > time2</pre>。如果{@code time2}为<code>null</code>，
     * 则视为最大。
     *
     * @param time1 时间字符串，格式为 yyyyMMddHHmmss，不足14位后补9
     * @param time2 时间字符串，格式为 yyyyMMddHHmmss，不足14位后补9
     * @return
     */
    public static boolean greaterThan(String time1, String time2) {
        if (StringUtils.isEmpty(time1)) {
            if (StringUtils.isEmpty(time2)) {
                return false;
            } else {
                return true;
            }
        } else {
            return time1.compareTo(time2) > 0;
        }
    }

    /**
     * 将<code>datetime</code>字符串时间转换为毫秒数
     * @param datetime  长度必须大于等于8而小于等于14，格式为 yyyyMMddHHmmss，不足14位后补0
     * @return
     */
    public static long toMilliseconds(String datetime){
        return parseDate(datetime).getTime();
    }

    /**
     * 将格式为{@link #DATETIME_FORMAT}的时间格式解析为Date对象,{@code datetime}的长度必须大于8小于14.
     * @param datetime
     * @return
     */
    public static Date parseDate(String datetime){
        Assert.notNull(datetime);
        Assert.isTrue(datetime.length() >= 4 && datetime.length() <= 14,"长度必须大于等于8而小于等于14");
        DateFormat dateFormat = SIMPLE_DATE_FORMAT;
        try {
            if(datetime.length() < 14){
                dateFormat = new SimpleDateFormat(DATETIME_FORMAT.substring(0, datetime.length()));
            }
            return dateFormat.parse(datetime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("入参datetime："+datetime+"解析异常，请检查格式必须为："+DATETIME_FORMAT);
        }
    }

    /**
     * 将字符串时间解析为对象
     * @param datetime
     * @param format
     * @return
     */
    public static Date parseDate(String datetime,String format){
        Assert.notNull(datetime);
        Assert.notNull(format);
        Assert.isTrue(datetime.length() == format.length(),"值和格式串的长度不一致");
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(datetime);
        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    MessageFormat.format("入参datetime：{1}解析异常，请检查格式必须为：{2}",datetime,format));
        }
    }
}
