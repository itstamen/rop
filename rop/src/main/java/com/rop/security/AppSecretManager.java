/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-3-1
 */
package com.rop.security;

/**
 * <pre>
 *    应用键管理器，可根据appKey获取对应的secret.
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface AppSecretManager {

    /**
     * 获取应用程序的密钥
     *
     * @param appKey
     * @return
     */
    String getSecret(String appKey);

    /**
     * 是否是合法的appKey
     *
     * @param appKey
     * @return
     */
    boolean isValidAppKey(String appKey);
}

