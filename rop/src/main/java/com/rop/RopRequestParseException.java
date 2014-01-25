/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-5-29
 */
package com.rop;

/**
 * <pre>
 *   对请求数据进行解析时发生异常
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopRequestParseException extends RopException {
    private String requestMessage;

    public RopRequestParseException(String requestMessage) {
        this(requestMessage, "");
    }

    public RopRequestParseException(String requestMessage, String message) {
        this(requestMessage, message, null);
    }

    public RopRequestParseException(String requestMessage, String message, Throwable cause) {
        super(message, cause);
        this.requestMessage = requestMessage;
    }

    public RopRequestParseException(String requestMessage, Throwable cause) {
        this(requestMessage, null, cause);
    }
}

