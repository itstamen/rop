/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-6
 */
package com.rop;

import com.rop.annotation.Temporary;

/**
 * <pre>
 *   所有请求对象应该通过扩展此抽象类实现
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractRopRequest implements RopRequest {

    @Temporary
    private RequestContext requestContext;

    @Override
    public RequestContext getRequestContext() {
        return requestContext;
    }

    public final void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }
}

