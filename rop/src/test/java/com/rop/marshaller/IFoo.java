/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-6
 */
package com.rop.marshaller;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-4-21
 */
public interface IFoo {

    @XmlTransient
    Integer getI1();
}
