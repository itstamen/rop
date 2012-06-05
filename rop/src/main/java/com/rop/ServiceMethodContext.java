/**
 *
 * 日    期：12-2-27
 */
package com.rop;

import java.util.Locale;

/**
 * <pre>
 *    接到服务请求后，将产生一个{@link ServiceMethodContext}上下文对象，它被本次请求直到返回响应的这个线程共享。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface ServiceMethodContext {

    /**
     * 获取Rop的上下文
     * @return
     */
    RopContext getRopContext();

    /**
     * 获取服务的方法
     *
     * @return
     */
    String getMethod();

    /**
     * 获取服务的版本号
     * @return
     */
    String getVersion();

    /**
     * 获取应用的appKey
     * @return
     */
    String getAppKey();

    /**
     * 获取会话的ID
     *
     * @return
     */
    String getSessionId();

    /**
     * 获取报文格式化参数
     * @return
     */
    String getFormat();
    
    /**
     * 获取响应的格式
     *
     * @return
     */
    MessageFormat getMessageFormat();    

    /**
     * 获取本地化对象
     *
     * @return
     */
    Locale getLocale();

    /**
     * 获取签名
     * @return
     */
    String getSign();

    /**
     * 设置服务开始时间
     * @param serviceBeginTime
     */
    void setServiceBeginTime(long serviceBeginTime);

    /**
     * 获取服务开始时间，单位为毫秒，为-1表示无意义
     * @return
     */
    long getServiceBeginTime();

    /**
     * 设置服务开始时间
     * @param serviceBeginTime
     */
    void setServiceEndTime(long serviceEndTime);

    /**
     * 获取服务结束时间，单位为毫秒，为-1表示无意义
     * @return
     */
    long getServiceEndTime();

    /**
     * 获取服务方法对应的ApiMethod对象信息
     * @return
     */
    ServiceMethodDefinition getServiceMethodDefinition();

    /**
     * 获取服务的处理者
     *
     * @return
     */
    ServiceMethodHandler getServiceMethodHandler();


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
     * @param ropResponse
     */
    void setRopResponse(RopResponse ropResponse);

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

    /**
     * 该方法是否开启签名的功能
     * @return
     */
    boolean isSignEnable();

}

