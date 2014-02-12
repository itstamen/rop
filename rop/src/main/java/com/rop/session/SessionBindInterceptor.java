/**
 * Copyright： 版权所有 违者必究 2013
 */
package com.rop.session;

import com.rop.AbstractInterceptor;
import com.rop.RopRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将{@link Session}绑定到{@link RopSessionHolder}中，默认注册。
 * @author : chenxh(quickselect@163.com)
 * @date: 13-10-16
 */
public class SessionBindInterceptor extends AbstractInterceptor {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeService(RopRequestContext ropRequestContext) {
        if(ropRequestContext.getSession() != null){
            RopSessionHolder.put(ropRequestContext.getSession());
            if(logger.isDebugEnabled()){
                logger.debug("会话绑定到{}中",RopSessionHolder.class.getCanonicalName());
            }
        }
    }
}
