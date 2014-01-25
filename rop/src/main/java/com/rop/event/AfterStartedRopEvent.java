/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-1
 */
package com.rop.event;

import com.rop.RopContext;

/**
 * <pre>
 *   在Rop框架初始化后产生的事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AfterStartedRopEvent extends RopEvent {

    public AfterStartedRopEvent(Object source, RopContext ropContext) {
        super(source, ropContext);
    }

}

