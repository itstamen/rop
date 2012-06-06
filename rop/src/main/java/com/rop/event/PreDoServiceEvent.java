/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.event;

import com.rop.RequestContext;

/**
 * <pre>
 *    在执行服务方法之前产生的事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class PreDoServiceEvent extends RopEvent {

    private RequestContext requestContext;

    public PreDoServiceEvent(Object source, RequestContext requestContext) {
        super(source, requestContext.getRopContext());
        this.requestContext = requestContext;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public long getServiceBeginTime() {
        return requestContext.getServiceBeginTime();
    }
}

