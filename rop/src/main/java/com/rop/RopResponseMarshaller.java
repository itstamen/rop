/**
 *
 * 日    期：12-2-27
 */
package com.rop;

import java.io.OutputStream;

/**
 * <pre>
 *   负责将请求方法返回的{@link RopResponse}流化为相应格式的内容。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopResponseMarshaller {
    void marshaller(RopResponse response, OutputStream outputStream);
}

