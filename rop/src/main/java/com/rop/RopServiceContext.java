/**
 *
 * 日    期：12-2-27
 */
package com.rop;

import com.rop.validation.MainError;

import java.util.Locale;

/**
 * <pre>
 *    处理服务请求的上下文。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopServiceContext {

    /**
     * 获取服务的方法
     *
     * @return
     */
    String getMethod();

    /**
     * 获取会话的ID
     *
     * @return
     */
    String getSessionId();

    /**
     * 获取本地化对象
     *
     * @return
     */
    Locale getLocale();

    /**
     * 获取服务的处理者
     *
     * @return
     */
    RopServiceHandler getRopServiceHandler();

    /**
     * 获取响应的格式
     *
     * @return
     */
    ResponseFormat getResponseFormat();

    /**
     * 返回响应对象
     *
     * @return
     */
    RopResponse getRopResponse();

    /**
     * 请求对象
     *
     * @return
     */
    RopRequest getRopRequest();

    /**
     * @return
     */
    String getAppKey();

    /**
     * @param ropRequest
     */
    void setRopRequest(RopRequest ropRequest);

    /**
     * @param ropResponse
     */
    void setRopResponse(RopResponse ropResponse);

    /**
     * 是否需要校验签名
     * @return
     */
    boolean isNeedCheckSign();

    /**
     * 获取特定属性
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * 设置属性的值
     *
     * @param name
     * @param value
     */
    void setAttribute(String name, Object value);

}

