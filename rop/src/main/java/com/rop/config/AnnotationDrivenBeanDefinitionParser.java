/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-4
 */
package com.rop.config;

import com.rop.impl.AnnotationServletServiceRouterFactoryBean;
import com.rop.impl.DefaultServiceAccessController;
import com.rop.security.DefaultInvokeTimesController;
import com.rop.security.FileBaseAppSecretManager;
import com.rop.session.DefaultSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
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
        serviceRouterDef.getPropertyValues().add("formattingConversionService", conversionServiceRbf);

        //会话管理器
        RuntimeBeanReference sessionManager = getSessionManager(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("sessionManager", sessionManager);

        //密钥管理器
        RuntimeBeanReference appSecretManager = getAppSecretManager(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("appSecretManager", appSecretManager);

        //服务访问控制器
        RuntimeBeanReference serviceAccessController = getServiceAccessController(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("serviceAccessController", serviceAccessController);

        //访问次数/频度控制器
        RuntimeBeanReference invokeTimesController = getInvokeTimesController(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("invokeTimesController", invokeTimesController);

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
            logger.debug("Please use {}/{} of schema to set error resources",
                         extErrorBasename,extErrorBasenames);
        }

    }

    private void setThreadFerry(Element element, RootBeanDefinition serviceRouterDef) {
        String threadFerryClassName = element.getAttribute("thread-ferry-class");
        if (StringUtils.hasText(threadFerryClassName)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("threadFerryClassName", threadFerryClassName);
        }
    }

    private RuntimeBeanReference getInvokeTimesController(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference invokeTimesControllerRef;
        if (element.hasAttribute("invoke-times-controller")) {
            invokeTimesControllerRef = new RuntimeBeanReference(element.getAttribute("invoke-times-controller"));
        } else {
            RootBeanDefinition invokeTimesControllerDef = new RootBeanDefinition(DefaultInvokeTimesController.class);
            invokeTimesControllerDef.setSource(source);
            invokeTimesControllerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String invokeTimesControllerName = parserContext.getReaderContext().registerWithGeneratedName(invokeTimesControllerDef);
            parserContext.registerComponent(new BeanComponentDefinition(invokeTimesControllerDef, invokeTimesControllerName));
            invokeTimesControllerRef = new RuntimeBeanReference(invokeTimesControllerName);
        }
        return invokeTimesControllerRef;
    }

    private RuntimeBeanReference getServiceAccessController(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference serviceAccessControllerRef;
        if (element.hasAttribute("service-access-controller")) {
            serviceAccessControllerRef = new RuntimeBeanReference(element.getAttribute("service-access-controller"));
        } else {
            RootBeanDefinition serviceAccessControllerDef = new RootBeanDefinition(DefaultServiceAccessController.class);
            serviceAccessControllerDef.setSource(source);
            serviceAccessControllerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String serviceAccessControllerName = parserContext.getReaderContext().registerWithGeneratedName(serviceAccessControllerDef);
            parserContext.registerComponent(new BeanComponentDefinition(serviceAccessControllerDef, serviceAccessControllerName));
            serviceAccessControllerRef = new RuntimeBeanReference(serviceAccessControllerName);
        }
        return serviceAccessControllerRef;
    }

    private RuntimeBeanReference getAppSecretManager(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference appSecretManagerRef;
        if (element.hasAttribute("app-secret-manager")) {
            appSecretManagerRef = new RuntimeBeanReference(element.getAttribute("app-secret-manager"));
        } else {
            RootBeanDefinition appSecretManagerDef = new RootBeanDefinition(FileBaseAppSecretManager.class);
            appSecretManagerDef.setSource(source);
            appSecretManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String appSecretManagerName = parserContext.getReaderContext().registerWithGeneratedName(appSecretManagerDef);
            parserContext.registerComponent(new BeanComponentDefinition(appSecretManagerDef, appSecretManagerName));
            appSecretManagerRef = new RuntimeBeanReference(appSecretManagerName);
        }
        return appSecretManagerRef;
    }

    private RuntimeBeanReference getSessionManager(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference sessionManagerRef;
        if (element.hasAttribute("session-manager")) {
            sessionManagerRef = new RuntimeBeanReference(element.getAttribute("session-manager"));
        } else {
            RootBeanDefinition sessionManagerDef = new RootBeanDefinition(DefaultSessionManager.class);
            sessionManagerDef.setSource(source);
            sessionManagerDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String sessionManagerName = parserContext.getReaderContext().registerWithGeneratedName(sessionManagerDef);
            parserContext.registerComponent(new BeanComponentDefinition(sessionManagerDef, sessionManagerName));
            sessionManagerRef = new RuntimeBeanReference(sessionManagerName);
        }
        return sessionManagerRef;
    }

    private RuntimeBeanReference getConversionService(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference conversionServiceRbf;
        if (element.hasAttribute("formatting-conversion-service")) {
            conversionServiceRbf = new RuntimeBeanReference(element.getAttribute("formatting-conversion-service"));
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition(FormattingConversionServiceFactoryBean.class);
            conversionDef.setSource(source);
            conversionDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String conversionName = parserContext.getReaderContext().registerWithGeneratedName(conversionDef);
            parserContext.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
            conversionServiceRbf = new RuntimeBeanReference(conversionName);
        }
        return conversionServiceRbf;
    }
}

