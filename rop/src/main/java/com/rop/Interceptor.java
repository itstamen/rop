/**
 * 版权声明： 版权所有 违者必究 2012
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
     * 在进行服务之前调用,如果在方法中往{@link RopRequestContext}设置了{@link RopResponse}（相当于已经产生了响应了）,
     * 所以服务将直接返回，不往下继续执行，反之服务会继续往下执行直到返回响应
     *
     * @param ropRequestContext
     */
    void beforeService(RopRequestContext ropRequestContext);

    /**
     * 在服务之后，响应之前调用
     *
     * @param ropRequestContext
     */
    void beforeResponse(RopRequestContext ropRequestContext);

    /**
     * 该方法返回true时才实施拦截，否则不拦截。可以通过{@link RopRequestContext}
     *
     * @param ropRequestContext
     * @return
     */
    boolean isMatch(RopRequestContext ropRequestContext);

    /**
     * 执行的顺序，整数值越小的越早执行
     *
     * @return
     */
    int getOrder();
}

