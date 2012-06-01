/**
 * 日期：12-2-10
 */
package com.rop;

/**
 * 支持的响应的格式类型
 */
public enum MessageFormat {

    XML, JSON;

    public static MessageFormat getFormat(String value) {
        if ("json".equalsIgnoreCase(value)) {
            return JSON;
        } else {
            return XML;
        }
    }
}
