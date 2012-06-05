/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.event;

import java.util.concurrent.Executor;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SimpleRopEventMulticaster extends AbstractRopEventMulticaster{

    private Executor executor;

    @Override
    public void multicastEvent(final RopEvent event) {
        for (final RopEventListener listener : getRopEventListeners(event)) {
     			Executor executor = getExecutor();
     			if (executor != null) {
     				executor.execute(new Runnable() {
     					@Override
     					public void run() {
     						listener.onRopEvent(event);
     					}
     				});
     			}
     			else {
     				listener.onRopEvent(event);
     			}
     		}
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}

