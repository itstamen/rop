/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-7
 */
package com.rop.config;

import com.rop.Interceptor;
import com.rop.RopContext;
import com.rop.event.RopEventListener;
import com.rop.impl.AnnotationServletServiceRouter;
import com.rop.sample.SampleAppSecretManager;
import com.rop.sample.SampleSecurityManager;
import com.rop.sample.SampleSessionChecker;
import com.rop.sample.request.Telephone;
import com.rop.validation.RopValidator;
import org.springframework.format.support.FormattingConversionService;
import org.testng.annotations.Test;
import org.unitils.UnitilsTestNG;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */

//@Transactional(TransactionMode.DISABLED)
public class RopNamespaceHandlerIT extends UnitilsTestNG {

    /**
     * 最简单的配置
     * @param serviceRouter
     */
    @Test
    @SpringBeanByType
    @SpringApplicationContext("com/rop/config/simplestRopConfig.xml")
    public void testSimplestConfig(AnnotationServletServiceRouter serviceRouter){
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
        assertNotNull(serviceRouter.getRopValidator());
        assertNotNull(serviceRouter.getRopEventMulticaster());
        assertNotNull(serviceRouter.getThreadPoolExecutor());
        assertEquals(serviceRouter.getThreadPoolExecutor().getCorePoolSize(), 1);
        assertEquals(serviceRouter.getThreadPoolExecutor().getMaximumPoolSize(), Integer.MAX_VALUE);
        assertEquals(serviceRouter.getThreadPoolExecutor().getKeepAliveTime(TimeUnit.SECONDS),120);
    }

    /**
     * 最简单的配置
     * @param serviceRouter
     */
    @Test
    @SpringBeanByType
    @SpringApplicationContext("com/rop/config/fullRopConfig.xml")
    public void testFullConfig(AnnotationServletServiceRouter serviceRouter){
        assertNotNull(serviceRouter);
        RopContext ropContext = serviceRouter.getRopContext();
        assertNotNull(ropContext);
        assertTrue(!ropContext.isSignEnable());
        assertNotNull(serviceRouter.getApplicationContext());
        assertEquals(serviceRouter.getExtErrorBasename(), "i18n/rop/sampleRopError");
        assertEquals(serviceRouter.getInterceptors().size(), 1);
        assertEquals(serviceRouter.getListeners().size(), 2);
        assertNotNull(serviceRouter.getFormattingConversionService());
        assertTrue(serviceRouter.getFormattingConversionService().canConvert(String.class,Telephone.class));

        assertFalse(serviceRouter.isSignEnable());
        RopValidator ropValidator = serviceRouter.getRopValidator();
        assertNotNull(ropValidator);
        assertTrue(ropValidator.getAppSecretManager() instanceof SampleAppSecretManager);
        assertTrue(ropValidator.getSecurityManager() instanceof SampleSecurityManager);
        assertTrue(ropValidator.getSessionChecker() instanceof SampleSessionChecker);

        assertNotNull(serviceRouter.getRopEventMulticaster());
        assertNotNull(serviceRouter.getThreadPoolExecutor());
        assertEquals(serviceRouter.getThreadPoolExecutor().getCorePoolSize(),2);
        assertEquals(serviceRouter.getThreadPoolExecutor().getMaximumPoolSize(), 100);
        assertEquals(serviceRouter.getThreadPoolExecutor().getKeepAliveTime(TimeUnit.SECONDS),200);
        assertEquals(serviceRouter.getThreadPoolExecutor().getQueue().remainingCapacity(),120);
        assertEquals(serviceRouter.getServiceTimeoutSeconds(),10);

        assertEquals(SysparamNames.getAppKey(),"a1");
        assertEquals(SysparamNames.getFormat(),"f1");
        assertEquals(SysparamNames.getSessionId(),"s1");
        assertEquals(SysparamNames.getSign(),"s2");
        assertEquals(SysparamNames.getLocale(),"l1");
        assertEquals(SysparamNames.getMethod(),"m1");
        assertEquals(SysparamNames.getVersion(),"v1");
    }
}

