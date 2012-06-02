/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.event;

import com.rop.ServiceMethodContext;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AfterDoServiceEvent extends RopEvent {

    private ServiceMethodContext serviceMethodContext;

    public AfterDoServiceEvent(Object source,ServiceMethodContext serviceMethodContext) {
        super(source, serviceMethodContext.getRopContext());
        this.serviceMethodContext = serviceMethodContext;
    }

    public long getServiceBeginTime() {
        return serviceMethodContext.getServiceBeginTime();
    }

    public long getServiceEndTime(){
        return serviceMethodContext.getServiceEndTime();
    }
}

