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
package com.rop.session;

/**
 * 认证请求
 * @author : chenxh
 * @date: 13-8-12
 */
public interface AuthRequest{

    /**
     * 认证的主体，对于常见的用户名/密码的认证请求来说，认证主体即是用户名。
     *
     * @return
     */
    Object getPrincipal();

    /**
     * 认证的凭据，对于常见的用户名/密码的认证请求来说，凭据即是密码。
     *
     * @return
     */
    Object getCredential();

    /**
     * 认证主体的其它信息，如IP地址，所属单位等信息
     *
     * @return
     */
    Object getDetail();
}
