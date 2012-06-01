/**
 *
 * 日    期：12-2-11
 */
package com.rop;

import com.rop.validation.AppSecretManager;
import com.rop.validation.SessionChecker;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *    BOP服务方法的处理者的注册表
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
     * @param serviceMethodHandler
     */
    void addBopServiceHandler(String methodName, ServiceMethodHandler serviceMethodHandler);

    /**
     * 获取服务处理器
     *
     * @param methodName
     * @return
     */
    ServiceMethodHandler getServiceMethodHandler(String methodName);

    /**
     * 是否是合法的服务方法
     *
     * @param methodName
     * @return
     */
    boolean isValidMethod(String methodName);

    /**
     * 获取所有的处理器列表
     * @return
     */
    Map<String,ServiceMethodHandler> getAllServiceMethodHandlers();

    /**
     * 获取Rop框架的配置
     * @return
     */
    RopConfig getRopConfig();

}

