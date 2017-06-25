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

import com.rop.RopRequestContext;

/**
 * <pre>
 *    在执行服务方法之前产生的事件
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class PreDoServiceEvent extends RopEvent {

	private static final long serialVersionUID = -3198265566192689456L;
	private RopRequestContext ropRequestContext;

    public PreDoServiceEvent(Object source, RopRequestContext ropRequestContext) {
        super(source, ropRequestContext.getRopContext());
        this.ropRequestContext = ropRequestContext;
    }

    public RopRequestContext getRopRequestContext() {
        return ropRequestContext;
    }

    public long getServiceBeginTime() {
        return ropRequestContext.getServiceBeginTime();
    }
}

