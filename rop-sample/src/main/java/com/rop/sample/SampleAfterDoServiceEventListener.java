/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.sample;

import com.rop.RopRequest;
import com.rop.RopRequestContext;
import com.rop.event.AfterDoServiceEvent;
import com.rop.event.RopEventListener;
import com.rop.marshaller.MessageMarshallerUtils;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SampleAfterDoServiceEventListener implements RopEventListener<AfterDoServiceEvent> {

    @Override
    public void onRopEvent(AfterDoServiceEvent ropEvent) {
        RopRequestContext ropRequestContext = ropEvent.getRopRequestContext();
        if(ropRequestContext != null && ropRequestContext.getRopRequest() != null){
            RopRequest ropRequest = ropRequestContext.getRopRequest();
            String message = MessageMarshallerUtils.asUrlString(ropRequest);
            System.out.println("message("+ropEvent.getServiceEndTime()+")"+message);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

