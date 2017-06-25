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

import java.util.*;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractRopEventMulticaster implements RopEventMulticaster {

    private Set<RopEventListener<RopEvent>> ropEventListeners = new HashSet<RopEventListener<RopEvent>>();

    private static final Map<Class<? extends RopEvent>, ListenerRegistry> cachedRopEventListeners =
            new HashMap<Class<? extends RopEvent>, ListenerRegistry>();


    public void removeAllRopListeners() {
        ropEventListeners.clear();
    }


    public void addRopListener(RopEventListener<RopEvent> listener) {
        ropEventListeners.add(listener);
    }


    public void removeRopListener(RopEventListener<RopEvent> listener) {
        ropEventListeners.remove(listener);
    }

    protected List<RopEventListener<RopEvent>> getRopEventListeners(RopEvent event) {
        Class<? extends RopEvent> eventType = event.getClass();
        if (!cachedRopEventListeners.containsKey(eventType)) {
            LinkedList<RopEventListener<RopEvent>> allListeners = new LinkedList<RopEventListener<RopEvent>>();
            if (ropEventListeners != null && ropEventListeners.size() > 0) {
                for (RopEventListener<RopEvent> ropEventListener : ropEventListeners) {
                    if (supportsEvent(ropEventListener, eventType)) {
                        allListeners.add(ropEventListener);
                    }
                }
                sortRopEventListener(allListeners);
            }
            ListenerRegistry listenerRegistry = new ListenerRegistry(allListeners);
            cachedRopEventListeners.put(eventType, listenerRegistry);
        }
        return cachedRopEventListeners.get(eventType).getRopEventListeners();
    }

	protected boolean supportsEvent(
            RopEventListener<RopEvent> listener, Class<? extends RopEvent> eventType) {
        SmartRopEventListener<RopEvent> smartListener = (listener instanceof SmartRopEventListener ?
                (SmartRopEventListener<RopEvent>) listener : new GenericRopEventAdapter(listener));
        return (smartListener.supportsEventType(eventType));
    }


    protected void sortRopEventListener(List<RopEventListener<RopEvent>> ropEventListeners) {
        Collections.sort(ropEventListeners, new Comparator<RopEventListener<RopEvent>>() {
            public int compare(RopEventListener<RopEvent> o1, RopEventListener<RopEvent> o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                } else if (o1.getOrder() < o2.getOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    private class ListenerRegistry {

        public List<RopEventListener<RopEvent>> ropEventListeners;

        private ListenerRegistry(List<RopEventListener<RopEvent>> ropEventListeners) {
            this.ropEventListeners = ropEventListeners;
        }

        public List<RopEventListener<RopEvent>> getRopEventListeners() {
            return ropEventListeners;
        }
    }
}

