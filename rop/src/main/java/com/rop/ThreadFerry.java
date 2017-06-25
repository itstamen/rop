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
 * 由于ROP采用异步方式工作，也即接收HTTP请求的线程和执行ROP方法的线程是不一样的，如果你在请求线程中通过
 * {@link ThreadLocal}绑定了一个本地线程变量，在ROP服务方法中将访问不到！因此必须进行在两线程之间进行
 * 变量的传递（也即线程摆渡），即在{@link #doInSrcThread()}方法中获取请求线程中的变量，放到
 * 实例变量中缓存起来，然后在{@link #doInDestThread()}方法中将实例变量的值手工设置到ROP方法执行线程的
 * 本地线程变量中。
 *
 * {@link ThreadFerry}每次都会创建一个实例，因此是线程安全的。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface ThreadFerry {

    /**
     * 在源线程中执行
     * @param
     */
    void doInSrcThread();

    /**
     * 在目标线程中执行
     */
    void doInDestThread();
}

