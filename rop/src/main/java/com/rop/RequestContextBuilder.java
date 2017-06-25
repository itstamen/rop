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
package com.rop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *    更改请求对象创建{@link RopRequestContext}实例,子类可以根据多种传输协议定义自己的创建器。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RequestContextBuilder {
    /**
     * 根据reqeuest及response请求响应对象，创建{@link RopRequestContext}实例。绑定系统参数，请求对象
     * @param ropContext
     * @param request
     * @param response
     * @return
     */
    RopRequestContext buildBySysParams(RopContext ropContext, HttpServletRequest request, HttpServletResponse response);

    /**
     * 绑定业务参数
     *
     * @param ropRequestContext
     */
    Object buildRopRequest(RopRequestContext ropRequestContext);
}

