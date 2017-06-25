/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rop.security;

import com.rop.RopException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

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
public class FileBaseAppSecretManager implements AppSecretManager {

    private static final String ROP_APP_SECRET_PROPERTIES = "rop.appSecret.properties";

    private String appSecretFile = ROP_APP_SECRET_PROPERTIES;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Properties properties;

    public String getSecret(String appKey) {
        if (properties == null) {
            try {
                DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
                Resource resource = resourceLoader.getResource(appSecretFile);
                properties =   PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException e) {
                throw new RopException("在类路径下找不到rop.appSecret.properties的应用密钥的属性文件", e);
            }
        }
        String secret = properties.getProperty(appKey);

        if (secret == null) {
            logger.error("不存在应用键为{0}的密钥,请检查应用密钥的配置文件。", appKey);
        }
        return secret;
    }

    public void setAppSecretFile(String appSecretFile) {
        this.appSecretFile = appSecretFile;
    }


    public boolean isValidAppKey(String appKey) {
        return getSecret(appKey) != null;
    }
}

