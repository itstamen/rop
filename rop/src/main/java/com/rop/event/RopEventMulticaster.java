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
package com.rop.event;

/**
 * <pre>
 *   注册事件监听器，发布事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     *
     * @param listener the listener to add
     */
    void addRopListener(RopEventListener<RopEvent> listener);

    /**
     * Remove a listener from the notification list.
     *
     * @param listener the listener to remove
     */
    void removeRopListener(RopEventListener<RopEvent> listener);

    /**
     * Remove all listeners registered with this multicaster.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are being registered.
     */
    void removeAllRopListeners();

    /**
     * Multicast the given application event to appropriate listeners.
     *
     * @param event the event to multicast
     */
    void multicastEvent(RopEvent event);
}

