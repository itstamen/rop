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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SimpleRopEventMulticaster extends AbstractRopEventMulticaster {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Executor executor;


    public void multicastEvent(final RopEvent event) {
        try {
            for (final RopEventListener<RopEvent> listener : getRopEventListeners(event)) {
                Executor executor = getExecutor();
                if (executor != null) {
                    executor.execute(new Runnable() {

                        public void run() {
                            listener.onRopEvent(event);
                        }
                    });
                } else {
                    listener.onRopEvent(event);
                }
            }
        } catch (Exception e) {
            logger.error("处理"+event.getClass().getName()+"事件发生异常",e);
        }
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}

