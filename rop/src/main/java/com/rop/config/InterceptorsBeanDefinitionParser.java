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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class InterceptorsBeanDefinitionParser implements BeanDefinitionParser {


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), parserContext.extractSource(element));
        parserContext.pushContainingComponent(compDefinition);
        List<Element> interceptors = DomUtils.getChildElementsByTagName(element, new String[]{"bean", "ref"});

        for (Element interceptor : interceptors) {
            RootBeanDefinition interceptorHolderDef = new RootBeanDefinition(InterceptorHolder.class);
            interceptorHolderDef.setSource(parserContext.extractSource(interceptor));
            interceptorHolderDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

            Object interceptorBean = parserContext.getDelegate().parsePropertySubElement(interceptor, null);
            interceptorHolderDef.getConstructorArgumentValues().addIndexedArgumentValue(0, interceptorBean);


            String beanName = parserContext.getReaderContext().registerWithGeneratedName(interceptorHolderDef);
            parserContext.registerComponent(new BeanComponentDefinition(interceptorHolderDef, beanName));
        }

        parserContext.popAndRegisterContainingComponent();
        return null;
    }
}

