/**
 *
 * 日    期：12-2-11
 */
package com.rop;

/**
 * <pre>
 *    BOP服务方法的处理者的注册表
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopServiceHandlerRegistry {

    /**
     * 注册一个服务处理器
     *
     * @param methodName
     * @param ropServiceHandler
     */
    void addBopServiceHandler(String methodName, RopServiceHandler ropServiceHandler);

    /**
     * 获取服务处理器
     *
     * @param methodName
     * @return
     */
    RopServiceHandler getBopServiceHandler(String methodName);

    /**
     * 是否是合法的服务方法
     *
     * @param methodName
     * @return
     */
    boolean isValidMethod(String methodName);

}

