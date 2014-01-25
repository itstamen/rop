/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-7-19
 */
package com.rop;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface ThreadFerry {

    /**
     * 在源线程中执行
     * @param obj
     */
    void doInSrcThread();

    /**
     * 在目标线程中执行
     */
    void doInDestThread();
}

