/**
 * Copyright： 版权所有 违者必究 2013
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
        return (T)ropSession.get();
    }
}
