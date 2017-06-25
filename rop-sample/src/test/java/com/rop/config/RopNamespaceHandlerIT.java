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

import com.rop.RopContext;
import com.rop.impl.AnnotationServletServiceRouter;
import com.rop.sample.request.Telephone;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */

@Transactional(TransactionMode.DISABLED)
@SpringApplicationContext("com/rop/config/simplestRopConfig.xml")
public class RopNamespaceHandlerIT extends UnitilsTestNG {

    @SpringBean("router")
    AnnotationServletServiceRouter serviceRouter;

    /**
     * 最简单的配置
     *
     * @param serviceRouter
     */
    @Test
    public void testSimplestConfig() {
        assertNotNull(serviceRouter);
        RopContext ropContext = serviceRouter.getRopContext();
        assertNotNull(ropContext);
        assertTrue(ropContext.isSignEnable());
        assertNotNull(serviceRouter.getApplicationContext());
        assertEquals(serviceRouter.getExtErrorBasename(), "i18n/rop/ropError");
        assertEquals(serviceRouter.getListeners().size(), 0);
        assertEquals(serviceRouter.getInterceptors().size(), 0);
        assertNotNull(serviceRouter.getFormattingConversionService());
        assertFalse(serviceRouter.getFormattingConversionService().canConvert(String.class, Telephone.class));
        assertTrue(serviceRouter.isSignEnable());
        assertNotNull(serviceRouter.getSecurityManager());
        assertNotNull(serviceRouter.getRopEventMulticaster());
        assertNotNull(serviceRouter.getThreadPoolExecutor());
        assertEquals(serviceRouter.getThreadPoolExecutor().getCorePoolSize(), 200);
        assertEquals(serviceRouter.getThreadPoolExecutor().getMaximumPoolSize(),500);
        assertEquals(serviceRouter.getThreadPoolExecutor().getKeepAliveTime(TimeUnit.SECONDS),5 * 60);
    }

//
//    AnnotationServletServiceRouter serviceRouter;

    /**
     * 最简单的配置
     *
     * @param serviceRouter
     */
//    @Test
//    @SpringBeanByType
//    @SpringApplicationContext("com/rop/config/fullRopConfig.xml")
//    public void testFullConfig(AnnotationServletServiceRouter serviceRouter) {
//        assertNotNull(serviceRouter);
//        RopContext ropContext = serviceRouter.getRopContext();
//        assertNotNull(ropContext);
//        assertTrue(!ropContext.isSignEnable());
//        assertNotNull(serviceRouter.getApplicationContext());
//        assertEquals(serviceRouter.getExtErrorBasename(), "i18n/rop/sampleRopError");
//        assertEquals(serviceRouter.getInterceptors().size(), 1);
//        assertEquals(serviceRouter.getListeners().size(), 2);
//        assertNotNull(serviceRouter.getFormattingConversionService());
//        assertTrue(serviceRouter.getFormattingConversionService().canConvert(String.class, Telephone.class));
//
//        assertFalse(serviceRouter.isSignEnable());
//        DefaultSecurityManager validator = (DefaultSecurityManager) serviceRouter.getSecurityManager();
//        assertNotNull(validator);
//        assertTrue(validator.getAppSecretManager() instanceof SampleAppSecretManager);
//        assertTrue(validator.getServiceAccessController() instanceof SampleServiceAccessController);
//
//        assertNotNull(serviceRouter.getRopEventMulticaster());
//        assertNotNull(serviceRouter.getThreadPoolExecutor());
//        assertEquals(serviceRouter.getThreadPoolExecutor().getCorePoolSize(), 2);
//        assertEquals(serviceRouter.getThreadPoolExecutor().getMaximumPoolSize(), 100);
//        assertEquals(serviceRouter.getThreadPoolExecutor().getKeepAliveTime(TimeUnit.SECONDS), 200);
//        assertEquals(serviceRouter.getThreadPoolExecutor().getQueue().remainingCapacity(), 120);
//        assertEquals(serviceRouter.getServiceTimeoutSeconds(), 10);
//
//        assertEquals(SystemParameterNames.getAppKey(), "a1");
//        assertEquals(SystemParameterNames.getFormat(), "f1");
//        assertEquals(SystemParameterNames.getSessionId(), "s1");
//        assertEquals(SystemParameterNames.getSign(), "s2");
//        assertEquals(SystemParameterNames.getLocale(), "l1");
//        assertEquals(SystemParameterNames.getMethod(), "m1");
//        assertEquals(SystemParameterNames.getVersion(), "v1");
//    }
}

