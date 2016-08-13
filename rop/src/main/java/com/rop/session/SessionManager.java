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
 * 会话管理器
 *
 * @author libinsong@gmail.com
 * @author 陈雄华
 */
public interface SessionManager {

    /**
     * 注册一个会话
     *
     * @param session
     */
    void addSession(String sessionId, Session session);

    /**
     * 从注册表中获取会话
     *
     * @param sessionId
     * @return
     */
    Session getSession(String sessionId);

    /**
     * 移除这个会话
     *
     * @param sessionId
     * @return
     */
    void removeSession(String sessionId);
}

