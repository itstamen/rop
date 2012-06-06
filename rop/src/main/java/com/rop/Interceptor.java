/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-4-25
 */
package com.rop;

/**
 * <pre>
 *   拦截器，将在服务之前，服务之后响应之前调用
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface Interceptor {

    /**
     * 在进行服务之前调用
     *
     * @param methodContext
     */
    void beforeService(RequestContext methodContext);

    /**
     * 在服务之后，响应之前调用
     *
     * @param methodContext
     */
    void beforeResponse(RequestContext methodContext);

    /**
     * 该方法返回true时才实施拦截，否则不拦截。可以通过{@link RequestContext}
     * @param methodContext
     * @return
     */
    boolean isMatch(RequestContext methodContext);

    /**
     *   执行的顺序，整数值越小的越早执行
     * @return
     */
    int getOrder();
}

