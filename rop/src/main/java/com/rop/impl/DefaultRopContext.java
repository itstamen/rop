/**
 *
 * 日    期：12-2-11
 */
package com.rop.impl;

import com.rop.*;
import com.rop.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

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
public class DefaultRopContext implements RopContext {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, ServiceMethodHandler> serviceHandlerMap = new HashMap<String, ServiceMethodHandler>();

    private final Set<String> serviceMethods = new HashSet<String>();

    private boolean signEnable;

    public DefaultRopContext(ApplicationContext context) {
        registerFromContext(context);
    }

    @Override
    public void addServiceMethod(String methodName, String version, ServiceMethodHandler serviceMethodHandler) {
        serviceMethods.add(methodName);
        serviceHandlerMap.put(ServiceMethodHandler.methodWithVersion(methodName, version), serviceMethodHandler);
    }

    @Override
    public ServiceMethodHandler getServiceMethodHandler(String methodName,String version) {
        return serviceHandlerMap.get(ServiceMethodHandler.methodWithVersion(methodName, version));
    }


    @Override
    public boolean isValidMethod(String methodName) {
        return serviceMethods.contains(methodName);
    }

    @Override
    public boolean isValidMethodVersion(String methodName, String version) {
        return serviceHandlerMap.containsKey(ServiceMethodHandler.methodWithVersion(methodName, version));
    }

    @Override
    public Map<String, ServiceMethodHandler> getAllServiceMethodHandlers() {
        return serviceHandlerMap;
    }

    @Override
    public boolean isSignEnable() {
        return signEnable;
    }

    public void setSignEnable(boolean signEnable) {
        this.signEnable = signEnable;
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
                            ReflectionUtils.makeAccessible(method);

                            ServiceMethod serviceMethod = method.getAnnotation(ServiceMethod.class);
                            ServiceMethodGroup serviceMethodGroup = method.getDeclaringClass().getAnnotation(ServiceMethodGroup.class);

                            ServiceMethodDefinition definition = null;
                            if (serviceMethodGroup != null) {
                                definition = buildServiceMethodDefinition(serviceMethodGroup, serviceMethod);
                            } else {
                                definition = buildServiceMethodDefinition(serviceMethod);
                            }
                            ServiceMethodHandler serviceMethodHandler = new ServiceMethodHandler();
                            serviceMethodHandler.setServiceMethodDefinition(definition);

                            //1.set handler
                            serviceMethodHandler.setHandler(context.getBean(beanName)); //handler
                            serviceMethodHandler.setHandlerMethod(method); //handler'method
                            if (method.getParameterTypes().length > 0) {//handler method's parameter
                                Class<?> aClass = method.getParameterTypes()[0];
                                Assert.isAssignable(RopRequest.class, aClass, "@ServiceMethod方法入参必须是RopRequest");
                                serviceMethodHandler.setRequestType((Class<? extends RopRequest>) aClass);
                            }

                            //2.set sign fieldNames
                            serviceMethodHandler.setIgnoreSignFieldNames(getIgnoreSignFieldNames(serviceMethodHandler.getRequestType()));

                            addServiceMethod(serviceMethod.value(), serviceMethod.version(), serviceMethodHandler);

                            if (logger.isDebugEnabled()) {
                                logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                        "#" + method.getName() + "(..)");
                            }
                        }
                    },
                    new ReflectionUtils.MethodFilter() {
                        public boolean matches(Method method) {
                            return AnnotationUtils.findAnnotation(method, ServiceMethod.class) != null;
                        }
                    }
            );
        }
        if (context.getParent() != null) {
            registerFromContext(context.getParent());
        }
        if (logger.isInfoEnabled()) {
            logger.info("共注册了" + serviceHandlerMap.size() + "个服务方法");
        }
    }

    private ServiceMethodDefinition buildServiceMethodDefinition(ServiceMethod serviceMethod) {
        ServiceMethodDefinition definition = new ServiceMethodDefinition();
        definition.setMethod(serviceMethod.value());
        definition.setMethodTitle(serviceMethod.title());
        definition.setMethodGroup(serviceMethod.group());
        definition.setMethodGroupTitle(serviceMethod.groupTitle());
        definition.setTags(serviceMethod.tags());
        definition.setTimeout(serviceMethod.timeout());
        definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethod.ignoreSign()));
        definition.setVersion(serviceMethod.version());
        definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethod.needInSession()));
        return definition;
    }

    private ServiceMethodDefinition buildServiceMethodDefinition(ServiceMethodGroup serviceMethodGroup, ServiceMethod serviceMethod) {
        ServiceMethodDefinition definition = new ServiceMethodDefinition();
        definition.setMethodGroup(serviceMethodGroup.value());
        definition.setMethodGroupTitle(serviceMethodGroup.title());
        definition.setTags(serviceMethodGroup.tags());
        definition.setTimeout(serviceMethodGroup.timeout());
        definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethodGroup.ignoreSign()));
        definition.setVersion(serviceMethodGroup.version());
        definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethodGroup.needInSession()));

        //如果ServiceMethod所提供的值和ServiceMethodGroup不一样，覆盖之
        definition.setMethod(serviceMethod.value());
        definition.setMethodTitle(serviceMethod.title());

        if (!ServiceMethodDefinition.DEFAULT_GROUP.equals(serviceMethod.group())) {
            definition.setMethodGroup(serviceMethod.group());
        }

        if (!ServiceMethodDefinition.DEFAULT_GROUP_TITLE.equals(serviceMethod.groupTitle())) {
            definition.setMethodGroupTitle(serviceMethod.groupTitle());
        }

        if (serviceMethod.tags() != null && serviceMethod.tags().length > 0) {
            definition.setTags(serviceMethod.tags());
        }

        if (serviceMethod.timeout() != -100) {
            definition.setTimeout(serviceMethod.timeout());
        }

        if (serviceMethod.ignoreSign() != IgnoreSignType.INVALID) {
            definition.setIgnoreSign(IgnoreSignType.isIgnoreSign(serviceMethod.ignoreSign()));
        }

        if (StringUtils.hasText(serviceMethod.version())) {
            definition.setVersion(serviceMethod.version());
        }

        if (serviceMethod.needInSession() != NeedInSessionType.INVALID) {
            definition.setNeedInSession(NeedInSessionType.isNeedInSession(serviceMethod.needInSession()));
        }

        return definition;
    }

    private List<String> getIgnoreSignFieldNames(Class<? extends RopRequest> requestType) {
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

