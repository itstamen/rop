/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.event;

import com.rop.ServiceMethodContext;

/**
 * <pre>
 *    在执行服务方法之前产生的事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class PreDoServiceEvent extends RopEvent {

    private ServiceMethodContext serviceMethodContext;

    public PreDoServiceEvent(Object source, ServiceMethodContext serviceMethodContext) {
        super(source, serviceMethodContext.getRopContext());
        this.serviceMethodContext = serviceMethodContext;
    }

    public ServiceMethodContext getServiceMethodContext() {
        return serviceMethodContext;
    }

    public long getServiceBeginTime() {
        return serviceMethodContext.getServiceBeginTime();
    }
}

