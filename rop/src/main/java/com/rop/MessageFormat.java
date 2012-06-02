/**
 * 日期：12-2-10
 */
package com.rop;

/**
 * 支持的响应的格式类型
 */
public enum MessageFormat {

    xml, json;

    public static MessageFormat getFormat(String value) {
        if ("json".equalsIgnoreCase(value)) {
            return json;
        } else {
            return xml;
        }
    }

    public static boolean isValidFormat(String value) {
        return xml.name().equalsIgnoreCase(value) || json.name().equalsIgnoreCase(value);
    }

}
