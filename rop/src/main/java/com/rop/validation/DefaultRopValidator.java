/**
 *
 * 日    期：12-2-13
 */
package com.rop.validation;

import com.rop.RopConfig;
import com.rop.SecurityManager;

/**
 * <pre>
 *    根据{@link RopConfig}中指定的校验组件构造校验器
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopValidator extends AbstractRopValidator {


    public DefaultRopValidator(RopConfig ropConfig) {

        //签名验证开关
        this.needCheckSign = ropConfig.isNeedCheckSign();

        //应用密钥管理器
        initAppSecretManager(ropConfig);

        //安全管理器
        initSecurityManager(ropConfig);

        //会话校验器
        initSessionChecker(ropConfig);
    }

    private void initSessionChecker(RopConfig ropConfig) {
        if (ropConfig.getSessionChecker() != null) {
            this.sessionChecker = ropConfig.getSessionChecker();
            if (logger.isDebugEnabled()) {
                logger.debug(SessionChecker.class.getName() + "使用" + this.sessionChecker.getClass().getName());
            }
        }
    }

    private void initSecurityManager(RopConfig ropConfig) {
        if (ropConfig.getSecurityManager() != null) {
            this.securityManager = ropConfig.getSecurityManager();
            if (logger.isDebugEnabled()) {
                logger.debug(SecurityManager.class.getName() + "使用" + this.securityManager.getClass().getName());
            }
        }
    }

    private void initAppSecretManager(RopConfig ropConfig) {
        if (ropConfig.getAppSecretManager() != null) {
            this.appSecretManager = ropConfig.getAppSecretManager();
            if (logger.isDebugEnabled()) {
                logger.debug(AppSecretManager.class.getName() + "使用" + this.appSecretManager.getClass().getName());
            }
        }
    }
}

