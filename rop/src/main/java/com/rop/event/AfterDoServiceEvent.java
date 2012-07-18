/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.event;

import com.rop.RequestContext;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AfterDoServiceEvent extends RopEvent {

    private RequestContext requestContext;

    public AfterDoServiceEvent(Object source, RequestContext requestContext) {
        super(source, requestContext.getRopContext());
        this.requestContext = requestContext;
    }

    public long getServiceBeginTime() {
        return requestContext.getServiceBeginTime();
    }

    public long getServiceEndTime() {
        return requestContext.getServiceEndTime();
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }
}

