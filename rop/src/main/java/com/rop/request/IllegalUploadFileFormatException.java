/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-8-1
 */
package com.rop.request;

/**
 * <pre>
 *    上传文件字符串转换时发生错误
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class IllegalUploadFileFormatException extends IllegalArgumentException {

    public IllegalUploadFileFormatException() {
        super();
    }

    public IllegalUploadFileFormatException(String s) {
        super(s);
    }

    public IllegalUploadFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalUploadFileFormatException(Throwable cause) {
        super(cause);
    }
}

