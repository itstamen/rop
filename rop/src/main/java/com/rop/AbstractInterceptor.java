/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-4-26
 */
package com.rop;

/**
 * <pre>
 *    抽象拦截器，实现类仅需覆盖特定的方法即可。 
 *    1.1 版本开始支持拦截器顺序可配置
 * </pre>
 * 
 * @author 陈雄华, angus
 * @version 1.1
 */
public abstract class AbstractInterceptor implements Interceptor {

    private int order = Integer.MAX_VALUE;

    public void beforeService(RopRequestContext ropRequestContext) {
    }

    public void beforeResponse(RopRequestContext ropRequestContext) {
    }

    @Override
    public boolean isMatch(RopRequestContext ropRequestContext) {
        return true;
    }

    /**
     * 放在拦截器链的最后
     * 
     * @return
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
