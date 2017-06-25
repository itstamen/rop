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
 *   拦截器，将在服务之前，服务之后响应之前调用
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface Interceptor {

    /**
     * 在进行服务之前调用,如果在方法中往{@link RopRequestContext}设置了{@link RopResponse}（相当于已经产生了响应了）,
     * 所以服务将直接返回，不往下继续执行，反之服务会继续往下执行直到返回响应
     *
     * @param ropRequestContext
     */
    void beforeService(RopRequestContext ropRequestContext);

    /**
     * 在服务之后，响应之前调用
     *
     * @param ropRequestContext
     */
    void beforeResponse(RopRequestContext ropRequestContext);

    /**
     * 该方法返回true时才实施拦截，否则不拦截。可以通过{@link RopRequestContext}
     *
     * @param ropRequestContext
     * @return
     */
    boolean isMatch(RopRequestContext ropRequestContext);

    /**
     * 执行的顺序，整数值越小的越早执行
     *
     * @return
     */
    int getOrder();
}

