/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-12
 */
package com.rop.impl;

import com.rop.MessageFormat;
import com.rop.RopContext;
import com.rop.ServiceMethodHandler;
import com.rop.config.SysParamNames;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServletRequestContextBuilderTest {

    /**
     * 正常情况下的系统参数绑定
     * @throws Exception
     */
    @Test
    public void testBuildBySysParams1() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);

        RopContext ropContext = mock(RopContext.class);
        ServiceMethodHandler methodHandler = mock(ServiceMethodHandler.class);
        when(ropContext.getServiceMethodHandler("method1","3.0")).thenReturn(methodHandler);

        //构造HttpServletRequest
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRemoteAddr("1.1.1.1");
        servletRequest.setParameter(SysParamNames.getAppKey(),"appKey1");
        servletRequest.setParameter(SysParamNames.getSessionId(),"sessionId1");
        servletRequest.setParameter(SysParamNames.getMethod(),"method1");
        servletRequest.setParameter(SysParamNames.getVersion(),"3.0");
        servletRequest.setParameter(SysParamNames.getLocale(),"en_UK");
        servletRequest.setParameter(SysParamNames.getFormat(),"xml");
        servletRequest.setParameter(SysParamNames.getSign(),"sign1");
        servletRequest.setParameter("param1","value1");
        servletRequest.setParameter("param2","value2");
        servletRequest.setParameter("param3","value3");

        //创建SimpleRequestContext
        SimpleRequestContext requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest);

        assertEquals(requestContext.getIp(),"1.1.1.1");
        assertEquals(requestContext.getAllParams().size(),10);
        assertEquals(requestContext.getParamValue("param1"),"value1");
        assertEquals(requestContext.getRawRequestObject(),servletRequest);

        assertEquals(requestContext.getAppKey(),"appKey1");
        assertEquals(requestContext.getSessionId(),"sessionId1");
        assertEquals(requestContext.getMethod(),"method1");
        assertEquals(requestContext.getVersion(),"3.0");
        assertEquals(requestContext.getLocale(), new Locale("zh","CN"));
        assertEquals(requestContext.getFormat(), "xml");
        assertEquals(requestContext.getMessageFormat(),MessageFormat.xml);
        assertEquals(requestContext.getSign(), "sign1");

        assertEquals(requestContext.getServiceMethodHandler(),methodHandler);
    }

    /**
     * 看错误的参数是否会被自动转为默认的
     * @throws Exception
     */
    @Test
    public void testBuildBySysParams2() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);
        RopContext ropContext = mock(RopContext.class);


        //构造HttpServletRequest
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setParameter(SysParamNames.getLocale(),"xxx");
        servletRequest.setParameter(SysParamNames.getFormat(),"xxx");


        //创建SimpleRequestContext
        SimpleRequestContext requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest);
        assertEquals(requestContext.getLocale(),Locale.SIMPLIFIED_CHINESE);
        assertEquals(requestContext.getFormat(), "xxx");
        assertEquals(requestContext.getMessageFormat(),MessageFormat.xml);

    }

    /**
     * 非{@link javax.servlet.http.HttpServletRequest}
     * @throws Exception
     */
    @Test(expectedExceptions ={IllegalArgumentException.class} )
    public void testBuildBySysParams3() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);

        RopContext ropContext = mock(RopContext.class);
        ServiceMethodHandler methodHandler = mock(ServiceMethodHandler.class);
        when(ropContext.getServiceMethodHandler("method1","3.0")).thenReturn(methodHandler);

        //创建SimpleRequestContext
        SimpleRequestContext requestContext = requestContextBuilder.buildBySysParams(ropContext, new Object());
    }

    @Test
    public void testBindBusinessParams() throws Exception {

    }
}

