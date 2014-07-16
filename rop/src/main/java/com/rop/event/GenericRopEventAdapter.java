/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-6-2
 */
package com.rop.event;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.GenericTypeResolver;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class GenericRopEventAdapter implements SmartRopEventListener {

    private final RopEventListener delegate;

    public GenericRopEventAdapter(RopEventListener delegate) {
        this.delegate = delegate;
    }


    public boolean supportsEventType(Class<? extends RopEvent> eventType) {
        Class typeArg = GenericTypeResolver.resolveTypeArgument(this.delegate.getClass(), RopEventListener.class);
        if (typeArg == null || typeArg.equals(RopEvent.class)) {
            Class targetClass = AopUtils.getTargetClass(this.delegate);
            if (targetClass != this.delegate.getClass()) {
                typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, RopEventListener.class);
            }
        }
        return (typeArg == null || typeArg.isAssignableFrom(eventType));
    }


    public void onRopEvent(RopEvent ropEvent) {
        this.delegate.onRopEvent(ropEvent);
    }


    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}

