/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-30
 */
package com.rop.client;

/**
 * <pre>
 *   对响应进行反流化
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopUnmarshaller {

    /**
     * 将字符串反序列化为相应的对象
     *
     * @param inputStream
     * @param objectType
     * @param <T>
     * @return
     */
    <T> T unmarshaller(String content, Class<T> objectType);
}

