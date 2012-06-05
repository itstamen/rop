/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-4
 */
package com.rop.config;

import com.rop.impl.AnnotationServletServiceRouter;
import com.rop.validation.DefaultRopValidator;
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

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);


        //注册ServiceRouter Bean
        RootBeanDefinition serviceRouterDef = new RootBeanDefinition(AnnotationServletServiceRouter.class);
        String serviceRouterName = parserContext.getReaderContext().registerWithGeneratedName(serviceRouterDef);
        parserContext.registerComponent(new BeanComponentDefinition(serviceRouterDef, serviceRouterName));

        //设置RopValidator
        RuntimeBeanReference ropValidator = getRopValidator(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("ropValidator", ropValidator);

        //设置TaskExecutor
        RuntimeBeanReference taskExecutor = getTaskExecutor(element, source, parserContext);
        if(taskExecutor != null){
            serviceRouterDef.getPropertyValues().add("taskExecutor",taskExecutor);
        }

        //设置signEnable
        String signEnable = element.getAttribute("sign-enable");
        if (StringUtils.hasText(signEnable)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("signEnable", Boolean.valueOf(signEnable));
        }

        //设置signEnable
        String extErrorBasename = element.getAttribute("ext-error-base-name");
        if (StringUtils.hasText(extErrorBasename)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("extErrorBasename",extErrorBasename);
        }

        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private RuntimeBeanReference getRopValidator(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference ropValidatorRbf;
        if (element.hasAttribute("rop-validator")) {
            ropValidatorRbf = new RuntimeBeanReference(element.getAttribute("rop-validator"));
            if(logger.isInfoEnabled()){
                logger.info("Rop装配一个自定义的RopValidator:"+ropValidatorRbf.getBeanName());
            }
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition(DefaultRopValidator.class);
            conversionDef.setSource(source);
            conversionDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String conversionName = parserContext.getReaderContext().registerWithGeneratedName(conversionDef);
            parserContext.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
            ropValidatorRbf = new RuntimeBeanReference(conversionName);
        }
        return ropValidatorRbf;
    }

    private RuntimeBeanReference getTaskExecutor(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference taskExecutorRef = null;
        if (element.hasAttribute("task-executor")) {
            taskExecutorRef = new RuntimeBeanReference(element.getAttribute("task-executor"));
            if(logger.isInfoEnabled()){
                logger.info("Rop装配一个异步任务执行器:"+taskExecutorRef.getBeanName());
            }
        }
        return taskExecutorRef;
    }
}

