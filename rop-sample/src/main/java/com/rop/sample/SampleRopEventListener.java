/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.sample;

import com.rop.event.PostInitializeEvent;
import com.rop.event.RopEventListener;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SampleRopEventListener implements RopEventListener<PostInitializeEvent>{

    @Override
    public void onRopEvent(PostInitializeEvent ropEvent) {
        System.out.println("execute SampleRopEventListener!");
    }

    @Override
    public int getOrder() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

