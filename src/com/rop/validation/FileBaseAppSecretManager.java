/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-3-1
 */
package com.rop.validation;

import com.rop.RopException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Properties;

/**
 * <pre>
 *   基于文件管理的应用密钥
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class FileBaseAppSecretManager implements  AppSecretManager{

    private Properties properties;


    public String getSecret(String appKey) {
        if(properties == null){
            try {
                properties = PropertiesLoaderUtils.loadAllProperties("rop.appSecret.properties");
            } catch (IOException e) {
                throw new RopException("在类路径下找不到rop.appSecret.properties的应用密钥的属性文件",e);
            }
        }
        String secret = properties.getProperty(appKey);
        Assert.notNull(secret,"appKey["+appKey+"]的密钥为空");
        return secret;
    }
}

