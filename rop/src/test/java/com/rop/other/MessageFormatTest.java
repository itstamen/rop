/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-6
 */
package com.rop.other;

import org.testng.annotations.Test;

import java.text.MessageFormat;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 2014/7/4
 */
public class MessageFormatTest {

    @Test
    public void testFormat(){
        String str = MessageFormat.format("aaa{0}bbb{1}", "X");
        System.out.println(str);
        str = MessageFormat.format("aaa bbb", "X","Y");
        System.out.println(str);
}
}
