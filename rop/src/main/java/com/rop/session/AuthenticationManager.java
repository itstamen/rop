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
 * @author : chenxh
 * @date: 13-8-12
 */
public interface AuthenticationManager {

    /**
     * 对{@link AuthRequest}进行认证，并产生{@link AuthResponse}。如果认证成功，需要生成{@link com.rop.session.Session}
     *
     * @param authRequest
     * @return
     */
    AuthResponse authenticate(AuthRequest authRequest);

    /**
     * 哪些应用使用这个认证管理器
     *
     * @return
     */
    String[] appkeys();

    /**
     * 是否是默认的认证器，如果某个应用没有对应的认证器，将采用默认的认证器
     *
     * @return
     */
    boolean isDefault();

}
