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
package com.rop.config;

import com.rop.impl.AnnotationServletServiceRouterFactoryBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

    public static final int DEFAULT_CORE_POOL_SIZE = 30;

    public static final int DEFAULT_MAX_POOL_SIZE = 120;

    public static final int DEFAULT_KEEP_ALIVE_SECONDS = 3 * 60;

    public static final int DEFAULT_QUENE_CAPACITY = 10;

    protected final Logger logger = LoggerFactory.getLogger(getClass());


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);

        //注册ServiceRouter Bean
        RootBeanDefinition serviceRouterDef = new RootBeanDefinition(AnnotationServletServiceRouterFactoryBean.class);
        String serviceRouterName = element.getAttribute("id");
        if (StringUtils.hasText(serviceRouterName)) {
            parserContext.getRegistry().registerBeanDefinition(serviceRouterName, serviceRouterDef);
        } else {
            serviceRouterName = parserContext.getReaderContext().registerWithGeneratedName(serviceRouterDef);
        }
        parserContext.registerComponent(new BeanComponentDefinition(serviceRouterDef, serviceRouterName));

        //设置formattingConversionService
        RuntimeBeanReference conversionServiceRbf = getConversionService(element, source, parserContext);
        if(conversionServiceRbf != null){
        	serviceRouterDef.getPropertyValues().add("formattingConversionService", conversionServiceRbf);
        }
        //会话管理器
        RuntimeBeanReference sessionManager = getSessionManager(element, source, parserContext);
        if(sessionManager != null){
        	serviceRouterDef.getPropertyValues().add("sessionManager", sessionManager);
        }
        //密钥管理器
        RuntimeBeanReference appSecretManager = getAppSecretManager(element, source, parserContext);
        if(appSecretManager != null){
        	serviceRouterDef.getPropertyValues().add("appSecretManager", appSecretManager);
        }
        //服务访问控制器
        RuntimeBeanReference serviceAccessController = getServiceAccessController(element, source, parserContext);
        if(serviceAccessController != null){
        	serviceRouterDef.getPropertyValues().add("serviceAccessController", serviceAccessController);
        }
        //访问次数/频度控制器
        RuntimeBeanReference invokeTimesController = getInvokeTimesController(element, source, parserContext);
        if(invokeTimesController != null){
        	serviceRouterDef.getPropertyValues().add("invokeTimesController", invokeTimesController);
        }
        //Xml格式转换器
        RuntimeBeanReference xmlMarshaller = getXmlMarshaller(element, source, parserContext);
        if(xmlMarshaller != null){
        	serviceRouterDef.getPropertyValues().add("xmlMarshaller", xmlMarshaller);
        }
        //JSON格式转换器
        RuntimeBeanReference jsonMarshaller = getJsonMarshaller(element, source, parserContext);
        if(jsonMarshaller != null){
        	serviceRouterDef.getPropertyValues().add("jsonMarshaller", jsonMarshaller);
        }
        //设置TaskExecutor
        setTaskExecutor(element, parserContext, source, serviceRouterDef);

        //设置signEnable
        setSignEnable(element, serviceRouterDef);

        //设置threadFerryClass
        setThreadFerry(element, serviceRouterDef);

        //设置国际化错误文件
        setExtErrorBaseNames(element, serviceRouterDef);

        //设置服务过期时间
        setServiceTimeout(element, serviceRouterDef);

        //设置文件上传配置信息
        setUploadFileSetting(element, serviceRouterDef);

        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private void setUploadFileSetting(Element element, RootBeanDefinition serviceRouterDef) {
        String uploadFileMaxSize = element.getAttribute("upload-file-max-size");
        if (StringUtils.hasText(uploadFileMaxSize)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("uploadFileMaxSize",uploadFileMaxSize);
        }

        String uploadFileTypes = element.getAttribute("upload-file-types");
        if (StringUtils.hasText(uploadFileTypes)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("uploadFileTypes", uploadFileTypes);
        }
    }

    private void setTaskExecutor(Element element, ParserContext parserContext, Object source, RootBeanDefinition serviceRouterDef) {
        RootBeanDefinition taskExecutorDef =
                new RootBeanDefinition(org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean.class);
        String taskExecutorName = parserContext.getReaderContext().registerWithGeneratedName(taskExecutorDef);

        String corePoolSize = element.getAttribute("core-pool-size");
        if (StringUtils.hasText(corePoolSize)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("corePoolSize", corePoolSize);
        } else {
            taskExecutorDef.getPropertyValues().addPropertyValue("corePoolSize", DEFAULT_CORE_POOL_SIZE);
        }

        String maxPoolSize = element.getAttribute("max-pool-size");
        if (StringUtils.hasText(maxPoolSize)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("maxPoolSize", maxPoolSize);
        } else {
            taskExecutorDef.getPropertyValues().addPropertyValue("maxPoolSize", DEFAULT_MAX_POOL_SIZE);
        }

        String keepAliveSeconds = element.getAttribute("keep-alive-seconds");
        if (StringUtils.hasText(keepAliveSeconds)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        } else {
            taskExecutorDef.getPropertyValues().addPropertyValue("keepAliveSeconds", DEFAULT_KEEP_ALIVE_SECONDS);
        }

        String queueCapacity = element.getAttribute("queue-capacity");
        if (StringUtils.hasText(queueCapacity)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("queueCapacity", queueCapacity);
        } else {
            taskExecutorDef.getPropertyValues().addPropertyValue("queueCapacity", DEFAULT_QUENE_CAPACITY);
        }

        parserContext.registerComponent(new BeanComponentDefinition(taskExecutorDef, taskExecutorName));
        RuntimeBeanReference taskExecutorBeanReference = new RuntimeBeanReference(taskExecutorName);
        serviceRouterDef.getPropertyValues().add("threadPoolExecutor", taskExecutorBeanReference);
    }

    private void setSignEnable(Element element, RootBeanDefinition serviceRouterDef) {
        String signEnable = element.getAttribute("sign-enable");
        if (StringUtils.hasText(signEnable)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("signEnable", signEnable);
        }
    }

    private void setServiceTimeout(Element element, RootBeanDefinition serviceRouterDef) {
        String serviceTimeoutSeconds = element.getAttribute("service-timeout-seconds");
        if (StringUtils.hasText(serviceTimeoutSeconds)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("serviceTimeoutSeconds", serviceTimeoutSeconds);
        }
    }

    private void setExtErrorBaseNames(Element element, RootBeanDefinition serviceRouterDef) {
        String extErrorBasename = element.getAttribute("ext-error-base-name");
        String extErrorBasenames = element.getAttribute("ext-error-base-names");
        if (StringUtils.hasText(extErrorBasenames)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("extErrorBasenames", extErrorBasenames);
        }
        if (StringUtils.hasText(extErrorBasename)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("extErrorBasename", extErrorBasename);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Please use {}/{} of schema to set error resources", extErrorBasename,extErrorBasenames);
        }
    }

    private void setThreadFerry(Element element, RootBeanDefinition serviceRouterDef) {
        String threadFerryClassName = element.getAttribute("thread-ferry-class");
        if (StringUtils.hasText(threadFerryClassName)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("threadFerryClassName", threadFerryClassName);
        }
    }

    private RuntimeBeanReference getInvokeTimesController(Element element, Object source, ParserContext parserContext) {
        if (element.hasAttribute("invoke-times-controller")) {
            return new RuntimeBeanReference(element.getAttribute("invoke-times-controller"));
        }
        return null;
    }

    private RuntimeBeanReference getServiceAccessController(Element element, Object source, ParserContext parserContext) {
        if (element.hasAttribute("service-access-controller")) {
            return new RuntimeBeanReference(element.getAttribute("service-access-controller"));
        }
        return null;
    }

    private RuntimeBeanReference getAppSecretManager(Element element, Object source, ParserContext parserContext) {
        if (element.hasAttribute("app-secret-manager")) {
            return new RuntimeBeanReference(element.getAttribute("app-secret-manager"));
        }
        return null;
    }

    private RuntimeBeanReference getSessionManager(Element element, Object source, ParserContext parserContext) {
        if (element.hasAttribute("session-manager")) {
            return new RuntimeBeanReference(element.getAttribute("session-manager"));
        }
        return null;
    }

    private RuntimeBeanReference getConversionService(Element element, Object source, ParserContext parserContext) {
        if (element.hasAttribute("formatting-conversion-service")) {
            return new RuntimeBeanReference(element.getAttribute("formatting-conversion-service"));
        }
        return null;
    }
    
    private RuntimeBeanReference getXmlMarshaller(Element element, Object source, ParserContext parserContext){
    	if(element.hasAttribute("xml-marshaller")){
    		return new RuntimeBeanReference(element.getAttribute("xml-marshaller"));
    	}
    	return null;
    }
    
    private RuntimeBeanReference getJsonMarshaller(Element element, Object source, ParserContext parserContext){
    	if(element.hasAttribute("json-marshaller")){
    		return new RuntimeBeanReference(element.getAttribute("json-marshaller"));
    	}
    	return null;
    }
}

