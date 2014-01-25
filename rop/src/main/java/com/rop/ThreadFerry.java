/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-7-19
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

