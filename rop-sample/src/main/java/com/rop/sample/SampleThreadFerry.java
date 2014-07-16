/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-7-20
 */
package com.rop.sample;

import com.rop.ThreadFerry;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SampleThreadFerry implements ThreadFerry{


    public void doInSrcThread() {
        System.out.println("doInSrcThread:"+Thread.currentThread().getId());
    }


    public void doInDestThread() {
        System.out.println("doInSrcThread:"+Thread.currentThread().getId());
    }
}

