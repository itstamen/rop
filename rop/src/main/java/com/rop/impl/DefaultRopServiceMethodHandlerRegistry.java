/**
 *
 * 日    期：12-2-11
 */
package com.rop.impl;

import com.rop.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <pre>
 *    BOP服务器的注册表
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopServiceMethodHandlerRegistry implements RopServiceHandlerRegistry {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, RopServiceHandler> bopServiceHandlerMap = new LinkedHashMap<String, RopServiceHandler>();

    public DefaultRopServiceMethodHandlerRegistry(ApplicationContext context) {
        registerFromContext(context);
    }

    public void addBopServiceHandler(String methodName, RopServiceHandler ropServiceHandler) {
        bopServiceHandlerMap.put(methodName, ropServiceHandler);
    }

    public RopServiceHandler getBopServiceHandler(String methodName) {
        return bopServiceHandlerMap.get(methodName);
    }

    public boolean isValidMethod(String methodName) {
        return bopServiceHandlerMap.containsKey(methodName);
    }

    /**
     * 检查上下文中的BOP服务方法
     *
     * @throws org.springframework.beans.BeansException
     *
     */
    private void registerFromContext(final ApplicationContext context) throws BeansException {
        if (logger.isDebugEnabled()) {
            logger.debug("对Spring上下文中的Bean进行扫描，查找ROP服务方法: " + context);
        }
        String[] beanNames = context.getBeanNamesForType(Object.class);
        for (final String beanName : beanNames) {
            Class<?> handlerType = context.getType(beanName);
            ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback() {
                        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                            ApiMethod apiMethod = method.getAnnotation(ApiMethod.class);
                            ReflectionUtils.makeAccessible(method);

                            RopServiceHandler ropServiceHandler = new RopServiceHandler();

                            //1.set handler
                            ropServiceHandler.setHandler(context.getBean(beanName)); //handler
                            ropServiceHandler.setHandlerMethod(method); //handler'method
                            if (method.getParameterTypes().length > 0) {//handler method's parameter
                                Class<?> aClass = method.getParameterTypes()[0];
                                Assert.isAssignable(RopRequest.class, aClass, "@BopServiceMethod的方法入参必须是");
                                ropServiceHandler.setRequestType((Class<? extends RopRequest>) aClass);
                            }

                            //2.set sign fieldNames
                            ropServiceHandler.setIgnoreSignFieldNames(getIgnoreSignFieldNames(ropServiceHandler.getRequestType()));

                            //3.set service name
                            ropServiceHandler.setNeedInSession(apiMethod.needInSession());

                            addBopServiceHandler(apiMethod.value(), ropServiceHandler);
                            if (logger.isDebugEnabled()) {
                                logger.debug("发现并注册一个BOP服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                        "#" + method.getName() + "(..)");
                            }
                        }
                    },
                    new ReflectionUtils.MethodFilter() {
                        public boolean matches(Method method) {
                            return AnnotationUtils.findAnnotation(method, ApiMethod.class) != null;
                        }
                    }
            );
        }
        if(context.getParent() != null){
            registerFromContext(context.getParent());
        }
        if (logger.isInfoEnabled()) {
            logger.info("共注册了"+bopServiceHandlerMap.size()+"个服务方法");
        }
    }

    private List<String> getIgnoreSignFieldNames(Class requestType) {
        if (logger.isDebugEnabled()) {
            logger.debug("获取" + requestType.getCanonicalName() + "需要签名的属性");
        }
        final ArrayList<String> igoreSignFieldNames = new ArrayList<String>(10);
        ReflectionUtils.doWithFields(requestType, new ReflectionUtils.FieldCallback() {
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        igoreSignFieldNames.add(field.getName());
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    public boolean matches(Field field) {
                        IgnoreSign ignoreSign = field.getAnnotation(IgnoreSign.class);
                        return ignoreSign != null;
                    }
                }
        );
        if (logger.isDebugEnabled()) {
            logger.debug(requestType.getCanonicalName() + "需要签名的属性:" + igoreSignFieldNames.toString());
        }
        return igoreSignFieldNames;
    }
}

