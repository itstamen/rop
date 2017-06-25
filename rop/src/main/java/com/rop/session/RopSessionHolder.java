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
 * 线程绑定的会话执有器，使请求线程范围的调用堆栈的所有对象都可以通过{@link #get()}这个静态方法获取会话。
 * {@link RopSessionHolder}依赖于{@link SessionBindInterceptor}拦截器工作。
 * @author : chenxh(quickselect@163.com)
 * @date: 13-10-16
 */
public class RopSessionHolder {

    private static  ThreadLocal<Session> ropSession = new ThreadLocal<Session>();

    public static  void put(Session session){
        ropSession.set(session);
    }
    public static Session get(){
        return ropSession.get();
    }

    public static <T> T get(Class<T> sessionClazz){
        return sessionClazz.cast(ropSession.get());
    }
}
