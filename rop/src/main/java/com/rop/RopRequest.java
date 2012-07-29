/**
 *
 * 日    期：12-2-10
 */
package com.rop;

/**
 * <pre>
 *    ROP服务的请求对象，请求方法的入参必须是继承于该类。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopRequest {

    /**
     * 获取服务方法的上下文
     *
     * @return
     */
    RopRequestContext getRopRequestContext();

}

