/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop.event;

import com.rop.RopConfig;
import com.rop.RopContext;
import com.rop.ServiceMethodHandler;

import java.util.EventObject;
import java.util.Map;

/**
 * <pre>
 *   在Rop框架初始化时发生该事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class OnInitializeEvent extends EventObject {

    private RopContext ropContext;

    public OnInitializeEvent(Object source, RopContext ropContext) {
        super(source);
        this.ropContext = ropContext;
    }

    public RopContext getRopContext() {
        return ropContext;
    }
}

