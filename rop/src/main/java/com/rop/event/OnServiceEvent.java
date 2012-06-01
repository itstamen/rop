/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.event;

import com.rop.RopRequest;
import com.rop.RopResponse;

import java.util.EventObject;

/**
 * <pre>
 *    在响应服务时产生的事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class OnServiceEvent extends EventObject {

    private long serviceBeginTime ;

    private long serviceEndTime;

    public OnServiceEvent(Object source) {
        super(source);
    }
}

