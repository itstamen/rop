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
package com.rop.impl;

import com.rop.request.RopRequestMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class FormattingConversionServiceTest {

    @Test
    public void testDoBind() {
        FormattingConversionServiceFactoryBean serviceFactoryBean = new FormattingConversionServiceFactoryBean();
        Set<Object> converters = new HashSet<Object>();
        converters.add(new RopRequestMessageConverter());
        serviceFactoryBean.setConverters(converters);

        serviceFactoryBean.afterPropertiesSet();
        ConversionService conversionService = serviceFactoryBean.getObject();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("userName", "tom");
        request.setParameter("address",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<address zoneCode=\"1\" doorCode=\"002\">\n" +
                        "  <streets>\n" +
                        "    <street no=\"001\" name=\"street1\"/>\n" +
                        "    <street no=\"002\" name=\"street2\"/>\n" +
                        "  </streets>\n" +
                        "</address>");

        CreateUserRequest bindObject = BeanUtils.instantiateClass(CreateUserRequest.class);
        ServletRequestDataBinder dataBinder = new ServletRequestDataBinder(bindObject, "bindObject");
        dataBinder.setConversionService(conversionService);
        dataBinder.setValidator(getValidator());
        dataBinder.bind(request);
        dataBinder.validate();

        assertTrue(dataBinder.getBindingResult().hasErrors());
        assertEquals(dataBinder.getBindingResult().getErrorCount(), 2);
        CreateUserRequest createUserRequest = (CreateUserRequest) dataBinder.getBindingResult().getTarget();
        assertNotNull(createUserRequest.getAddress());
        assertNotNull(createUserRequest.getAddress().getStreets());
        assertTrue(createUserRequest.getAddress().getStreets().size() > 0);
    }

    private Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }
}

