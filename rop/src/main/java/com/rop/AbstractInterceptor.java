/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-4-26
 */
package com.rop;

/**
 * <pre>
 *    抽象拦截器，实现类仅需覆盖特定的方法即可。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractInterceptor implements Interceptor {

    public void beforeService(RopRequestContext ropRequestContext) {
    }


    public void beforeResponse(RopRequestContext ropRequestContext) {
    }


    public boolean isMatch(RopRequestContext ropRequestContext) {
        return true;
    }

    /**
     * 放在拦截器链的最后
     *
     * @return
     */
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

