/**
 *
 * 日    期：12-2-11
 */
package com.rop;

import com.rop.session.SessionManager;

import java.util.Map;

/**
 * <pre>
 *    ROP服务方法的处理者的注册表
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopContext {

    /**
     * 注册一个服务处理器
     *
     * @param methodName
     * @param version
     * @param serviceMethodHandler
     */
    void addServiceMethod(String methodName, String version, ServiceMethodHandler serviceMethodHandler);

    /**
     * 获取服务处理器
     *
     * @param methodName
     * @return
     */
    ServiceMethodHandler getServiceMethodHandler(String methodName, String version);

    /**
     * 是否是合法的服务方法
     *
     * @param methodName
     * @return
     */
    boolean isValidMethod(String methodName);

    /**
     * 是否存在对应的服务方法的版本号
     *
     * @param methodName
     * @param version
     * @return
     */
    boolean isValidVersion(String methodName, String version);


    /**
     * 服务方法的版本是否已经弃用
     *
     * @param methodName
     * @param version
     * @return
     */
    boolean isVersionObsoleted(String methodName, String version);

    /**
     * 获取所有的处理器列表
     *
     * @return
     */
    Map<String, ServiceMethodHandler> getAllServiceMethodHandlers();

    /**
     * 是开启签名功能
     *
     * @return
     */
    boolean isSignEnable();

    /**
     * 获取会话管理器
     * @return
     */
    SessionManager getSessionManager();

}

