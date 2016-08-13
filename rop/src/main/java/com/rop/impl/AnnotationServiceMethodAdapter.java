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
package com.rop.impl;

import com.rop.RopContext;
import com.rop.RopRequestContext;
import com.rop.ServiceMethodAdapter;
import com.rop.ServiceMethodHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <pre>
 *    通过该服务方法适配器调用目标的服务方法
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationServiceMethodAdapter implements ServiceMethodAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 调用ROP服务方法
     *
     * @param ropRequest
     * @return
     */
    public Object invokeServiceMethod(Object ropRequest, RopRequestContext context) {
        try {
            //分析上下文中的错误
            ServiceMethodHandler serviceMethodHandler = context.getServiceMethodHandler();
            if (logger.isDebugEnabled()) {
                logger.debug("执行" + serviceMethodHandler.getHandler().getClass() +
                        "." + serviceMethodHandler.getHandlerMethod().getName());
            }
            Method method = serviceMethodHandler.getHandlerMethod();
            Class<?>[] classes = method.getParameterTypes();
            if(classes == null || classes.length <= 0){
            	return method.invoke(serviceMethodHandler.getHandler());
            }else{
            	Object[] args = new Object[classes.length];
            	for(int i = 0; i < args.length; i++){
            		Class<?> type = classes[i];
            		if(ropRequest != null && ropRequest.getClass().isAssignableFrom(type)){
            			args[i] = ropRequest;
            		}else if(type.isAssignableFrom(RopRequestContext.class)){
            			args[i] = context;
            		}else if(type.isAssignableFrom(HttpServletRequest.class)){
            			args[i] = context.getRawRequestObject();
            		}else if(type.isAssignableFrom(HttpServletResponse.class)){
            			args[i] = context.getRawResponseObject();
            		}else if(type.isAssignableFrom(RopContext.class)){
            			args[i] = context.getRopContext();
            		}
            	}
            	return method.invoke(serviceMethodHandler.getHandler(), args);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException inve = (InvocationTargetException) e;
                throw new RuntimeException(inve.getTargetException());
            } else {
                throw new RuntimeException(e);
            }
        }
    }

}

