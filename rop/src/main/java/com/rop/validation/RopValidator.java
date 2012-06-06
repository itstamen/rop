/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.RequestContext;
import com.rop.SecurityManager;

/**
 * <pre>
 *   负责对请求数据、会话、业务安全、应用密钥安全进行检查并返回相应的错误
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
    MainError validate(RequestContext methodContext);

    /**
     * 获取安全管理器
     * @return
     */
    SecurityManager getSecurityManager();

    /**
     * 获取应用密钥管理器
     * @return
     */
    AppSecretManager getAppSecretManager();

    /**
     * 获取会话校验器
     * @return
     */
    SessionChecker getSessionChecker();
}

