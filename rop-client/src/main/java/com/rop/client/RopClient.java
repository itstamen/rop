/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-29
 */
package com.rop.client;

import com.rop.RopRequest;
import com.rop.RopResponse;
import com.rop.annotation.HttpAction;


/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopClient {

    /**
     * 如果不需要在会话环境下执行，则使用该方法
     * @param ropRequest
     * @param methodName
     * @param version
     * @return
     */
    <T extends RopResponse> CompositeResponse get(RopRequest ropRequest,
                                                  Class<T> ropResponseClass,String methodName, String version);

    /**
     * 如果需要在会话环境下执行，则使用sessionId转递会话的ID
     * @param ropRequest
     * @param methodName
     * @param version
     * @param sessionId
     * @return
     */
    <T extends RopResponse> CompositeResponse get(RopRequest ropRequest,
                                                   Class<T> ropResponseClass,String methodName,String version,String sessionId);


    /**
     * 如果不需要在会话环境下执行，则使用该方法
     * @param ropRequest
     * @param methodName
     * @param version
     * @return
     */
    <T extends RopResponse> CompositeResponse post(RopRequest ropRequest,
                                                   Class<T> ropResponseClass,String methodName,String version);

    /**
     * 如果需要在会话环境下执行，则使用sessionId转递会话的ID
     * @param ropRequest
     * @param methodName
     * @param version
     * @param sessionId
     * @return
     */
    <T extends RopResponse> CompositeResponse post(RopRequest ropRequest,
                           Class<T> ropResponseClass,String methodName,String version,String sessionId);
}

