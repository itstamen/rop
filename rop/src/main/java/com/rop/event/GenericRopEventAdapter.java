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

import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class GenericRopEventAdapter implements SmartRopEventListener<RopEvent> {

    private final RopEventListener<RopEvent> delegate;

    public GenericRopEventAdapter(RopEventListener<RopEvent> delegate) {
        this.delegate = delegate;
    }

    public boolean supportsEventType(Class<? extends RopEvent> eventType) {
        Class<?> typeArg = GenericTypeResolver.resolveTypeArgument(this.delegate.getClass(), RopEventListener.class);
        if (typeArg == null || typeArg.equals(RopEvent.class)) {
            Class<?> targetClass = AopUtils.getTargetClass(this.delegate);
            if (targetClass != this.delegate.getClass()) {
                typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, RopEventListener.class);
            }
        }
        return (typeArg == null || typeArg.isAssignableFrom(eventType));
    }


    public void onRopEvent(RopEvent ropEvent) {
        this.delegate.onRopEvent(ropEvent);
    }


    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

