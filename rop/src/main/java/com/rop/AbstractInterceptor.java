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

/**
 * <pre>
 *    抽象拦截器，实现类仅需覆盖特定的方法即可。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public abstract class AbstractInterceptor implements Interceptor {

    public void beforeService(RopRequestContext ropRequestContext) {
    }


    public void beforeResponse(RopRequestContext ropRequestContext) {
    }


    public boolean isMatch(RopRequestContext ropRequestContext) {
        return true;
    }

    /**
     * 放在拦截器链的最后
     *
     * @return
     */
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

