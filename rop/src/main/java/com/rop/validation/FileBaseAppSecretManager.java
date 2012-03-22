/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-3-1
 */
package com.rop.validation;

import com.rop.RopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final String ROP_APP_SECRET_PROPERTIES = "rop.appSecret.properties";
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private Properties properties;

    public String getSecret(String appKey) {
        if(properties == null){
            try {
                properties = PropertiesLoaderUtils.loadAllProperties(ROP_APP_SECRET_PROPERTIES);
            } catch (IOException e) {
                throw new RopException("在类路径下找不到rop.appSecret.properties的应用密钥的属性文件",e);
            }
        }
        String secret = properties.getProperty(appKey);

        if(secret == null){
            logger.error("不存在应用键为{0}的密钥,请检查应用密钥的配置文件。",appKey);
        }
        return secret;
    }
}

