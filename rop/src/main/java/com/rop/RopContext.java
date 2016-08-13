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

import com.rop.session.SessionManager;

import java.util.Map;

/**
 * <pre>
 *    ROP服务方法的处理者的注册表
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopContext {

    /**
     * 注册一个服务处理器
     *
     * @param methodName
     * @param version
     * @param serviceMethodHandler
     */
    void addServiceMethod(String methodName, String version, ServiceMethodHandler serviceMethodHandler);

    /**
     * 获取服务处理器
     *
     * @param methodName
     * @return
     */
    ServiceMethodHandler getServiceMethodHandler(String methodName, String version);

    /**
     * 是否是合法的服务方法
     *
     * @param methodName
     * @return
     */
    boolean isValidMethod(String methodName);

    /**
     * 是否存在对应的服务方法的版本号
     *
     * @param methodName
     * @param version
     * @return
     */
    boolean isValidVersion(String methodName, String version);


    /**
     * 服务方法的版本是否已经弃用
     *
     * @param methodName
     * @param version
     * @return
     */
    boolean isVersionObsoleted(String methodName, String version);

    /**
     * 获取所有的处理器列表
     *
     * @return
     */
    Map<String, ServiceMethodHandler> getAllServiceMethodHandlers();

    /**
     * 是开启签名功能
     *
     * @return
     */
    boolean isSignEnable();

    /**
     * 获取会话管理器
     * @return
     */
    SessionManager getSessionManager();

}

