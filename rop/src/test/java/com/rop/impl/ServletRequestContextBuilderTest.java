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

import com.rop.MessageFormat;
import com.rop.RopContext;
import com.rop.ServiceMethodHandler;
import com.rop.config.SystemParameterNames;

import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServletRequestContextBuilderTest {

    @Test
    public void testIpParsed() {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);
        RopContext ropContext = mock(RopContext.class);

        //构造HttpServletRequest
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRemoteAddr("1.1.1.1");

        //创建SimpleRequestContext
        SimpleRopRequestContext requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest,null);
        assertEquals(requestContext.getIp(), "1.1.1.1");

        servletRequest.setRemoteAddr("1.1.1.1");
        servletRequest.addHeader(ServletRequestContextBuilder.X_FORWARDED_FOR, "null,2.2.2.2,3.3.3.3");
        requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest,null);
        assertEquals(requestContext.getIp(), "2.2.2.2");

        servletRequest.addHeader(ServletRequestContextBuilder.X_REAL_IP, "5.5.5.5");
        requestContext = requestContextBuilder.buildBySysParams(ropContext, servletRequest,null);
        assertEquals(requestContext.getIp(), "5.5.5.5");

    }

    /**
     * 正常情况下的系统参数绑定
     *
     * @throws Exception
     */
    @Test
    public void testBuildBySysParams1() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);

        RopContext ropContext = mock(RopContext.class);
        ServiceMethodHandler methodHandler = mock(ServiceMethodHandler.class);
        when(ropContext.getServiceMethodHandler("method1", "3.0")).thenReturn(methodHandler);

        //构造HttpServletRequest
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        servletRequest.setParameter(SystemParameterNames.getAppKey(), "appKey1");
        servletRequest.setParameter(SystemParameterNames.getSessionId(), "sessionId1");
        servletRequest.setParameter(SystemParameterNames.getMethod(), "method1");
        servletRequest.setParameter(SystemParameterNames.getVersion(), "3.0");
        servletRequest.setParameter(SystemParameterNames.getLocale(), "en_UK");
        servletRequest.setParameter(SystemParameterNames.getFormat(), "xml");
        servletRequest.setParameter(SystemParameterNames.getSign(), "sign1");
        servletRequest.setParameter("param1", "value1");
        servletRequest.setParameter("param2", "value2");
        servletRequest.setParameter("param3", "value3");

        //创建SimpleRequestContext
        SimpleRopRequestContext requestContext =
                requestContextBuilder.buildBySysParams(ropContext, servletRequest,null);

        assertEquals(requestContext.getAllParams().size(), 10);
        assertEquals(requestContext.getParamValue("param1"), "value1");
        assertEquals(requestContext.getRawRequestObject(), servletRequest);

        assertEquals(requestContext.getAppKey(), "appKey1");
        assertEquals(requestContext.getSessionId(), "sessionId1");
        assertEquals(requestContext.getMethod(), "method1");
        assertEquals(requestContext.getVersion(), "3.0");
        assertEquals(requestContext.getLocale(), new Locale("zh", "CN"));
        assertEquals(requestContext.getFormat(), "xml");
        assertEquals(requestContext.getMessageFormat(), MessageFormat.XML);
        assertEquals(requestContext.getSign(), "sign1");

        assertEquals(requestContext.getServiceMethodHandler(), methodHandler);
    }

    /**
     * 看错误的参数是否会被自动转为默认的
     *
     * @throws Exception
     */
    @Test
    public void testBuildBySysParams2() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);
        RopContext ropContext = mock(RopContext.class);


        //构造HttpServletRequest
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setParameter(SystemParameterNames.getLocale(), "xxx");
        servletRequest.setParameter(SystemParameterNames.getFormat(), "xxx");


        //创建SimpleRequestContext
        SimpleRopRequestContext requestContext =
                requestContextBuilder.buildBySysParams(ropContext, servletRequest,null);
        assertEquals(requestContext.getLocale(), Locale.SIMPLIFIED_CHINESE);
        assertEquals(requestContext.getFormat(), "xxx");
        assertEquals(requestContext.getMessageFormat(), MessageFormat.XML);

    }

    /**
     * 非{@link javax.servlet.http.HttpServletRequest}
     *
     * @throws Exception
     */
    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testBuildBySysParams3() throws Exception {
        FormattingConversionService conversionService = mock(FormattingConversionService.class);
        ServletRequestContextBuilder requestContextBuilder = new ServletRequestContextBuilder(conversionService);

        RopContext ropContext = mock(RopContext.class);
        ServiceMethodHandler methodHandler = mock(ServiceMethodHandler.class);
        when(ropContext.getServiceMethodHandler("method1", "3.0")).thenReturn(methodHandler);
       
        //创建SimpleRequestContext
        SimpleRopRequestContext requestContext =
                requestContextBuilder.buildBySysParams(ropContext, new MockHttpServletRequest(), null);
    }

    @Test
    public void testBindBusinessParams() throws Exception {

    }
}

