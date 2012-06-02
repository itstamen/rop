/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-2
 */
package com.rop.sample;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class MySmartApplicationListener<ApplicationContextEvent> implements SmartApplicationListener {
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getOrder() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

