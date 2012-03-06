/**
 *
 * 日    期：12-2-11
 */
package com.rop;

/**
 * <pre>
 *    通过该适配器以统一的方式调用BOP方法
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopServiceMethodAdapter {
    /**
     * 调用服务方法
     *
     * @param bopServiceHandler
     * @param webRequest
     * @return
     */
    RopResponse invokeServiceMethod(RopServiceContext context);

}

