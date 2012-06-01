/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.ServiceMethodContext;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopValidator {

    /**
     * 对请求服务的上下文进行检查校验
     *
     * @param methodContext
     * @return
     */
    MainError validate(ServiceMethodContext methodContext);
}

