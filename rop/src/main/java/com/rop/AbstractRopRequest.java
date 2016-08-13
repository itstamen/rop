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

import com.rop.annotation.Temporary;

/**
 * <pre>
 *   所有请求对象应该通过扩展此抽象类实现
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractRopRequest implements RopRequest {

    @Temporary
    private RopRequestContext ropRequestContext;


    public RopRequestContext getRopRequestContext() {
        return ropRequestContext;
    }

    public final void setRopRequestContext(RopRequestContext ropRequestContext) {
        this.ropRequestContext = ropRequestContext;
    }
}

