/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-7
 */
package com.rop.impl;

import com.rop.Interceptor;
import com.rop.ThreadFerry;
import com.rop.config.InterceptorHolder;
import com.rop.config.RopEventListenerHodler;
import com.rop.event.RopEventListener;
import com.rop.session.SessionManager;
import com.rop.validation.AppSecretManager;
import com.rop.SecurityManager;
import com.rop.validation.DefaultRopValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.format.support.FormattingConversionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationServletServiceRouterFactoryBean
        implements FactoryBean, ApplicationContextAware, InitializingBean, DisposableBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    private ThreadPoolExecutor threadPoolExecutor;

    private SessionManager sessionManager;

    private AppSecretManager appSecretManager;

    private SecurityManager securityManager;

    private boolean signEnable = true;

    private String extErrorBasename;

    private int serviceTimeoutSeconds = -1;
    
    private  Class<? extends ThreadFerry> threadFerryClass;

    private FormattingConversionService formattingConversionService;

    private AnnotationServletServiceRouter serviceRouter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void destroy() throws Exception {
        serviceRouter.shutdown();
    }

    @Override
    public Class<?> getObjectType() {
        return AnnotationServletServiceRouter.class;
    }

    @Override
    public AnnotationServletServiceRouter getObject() throws Exception {
        return this.serviceRouter;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setThreadFerryClass(Class<? extends ThreadFerry> threadFerryClass) {
        this.threadFerryClass = threadFerryClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //实例化一个AnnotationServletServiceRouter
        serviceRouter = new AnnotationServletServiceRouter();

        //设置属性
        if (extErrorBasename != null) {
            serviceRouter.setExtErrorBasename(extErrorBasename);
        }
        DefaultRopValidator ropValidator = BeanUtils.instantiate(DefaultRopValidator.class);

        ropValidator.setSessionManager(sessionManager);
        ropValidator.setAppSecretManager(appSecretManager);
        ropValidator.setSecurityManager(securityManager);

        serviceRouter.setRopValidator(ropValidator);
        serviceRouter.setThreadPoolExecutor(threadPoolExecutor);
        serviceRouter.setSignEnable(signEnable);
        serviceRouter.setServiceTimeoutSeconds(serviceTimeoutSeconds);
        serviceRouter.setFormattingConversionService(formattingConversionService);
        serviceRouter.setSessionManager(sessionManager);
        serviceRouter.setThreadFerryClass(threadFerryClass);

        //注册拦截器
        ArrayList<Interceptor> interceptors = getInterceptors();
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                serviceRouter.addInterceptor(interceptor);
            }
            if (logger.isInfoEnabled()) {
                logger.info("共注册了" + interceptors.size() + "拦截器.");
            }
        }

        //注册监听器
        ArrayList<RopEventListener> listeners = getListeners();
        if (listeners != null) {
            for (RopEventListener listener : listeners) {
                serviceRouter.addListener(listener);
            }
            if (logger.isInfoEnabled()) {
                logger.info("共注册了" + listeners.size() + "事件监听器.");
            }
        }

        //设置Spring上下文信息
        serviceRouter.setApplicationContext(this.applicationContext);

        //启动之
        serviceRouter.startup();
    }

    private ArrayList<Interceptor> getInterceptors() {
        Map<String, InterceptorHolder> interceptorMap = this.applicationContext.getBeansOfType(InterceptorHolder.class);
        if (interceptorMap != null && interceptorMap.size() > 0) {
            ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>(interceptorMap.size());

            //从Spring容器中获取Interceptor
            for (InterceptorHolder interceptorHolder : interceptorMap.values()) {
                interceptors.add(interceptorHolder.getInterceptor());
            }

            //根据getOrder()值排序
            Collections.sort(interceptors, new Comparator<Interceptor>() {
                public int compare(Interceptor o1, Interceptor o2) {
                    if (o1.getOrder() > o2.getOrder()) {
                        return 1;
                    } else if (o1.getOrder() < o2.getOrder()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            return interceptors;
        } else {
            return null;
        }
    }

    private ArrayList<RopEventListener> getListeners() {
        Map<String, RopEventListenerHodler> listenerMap = this.applicationContext.getBeansOfType(RopEventListenerHodler.class);
        if (listenerMap != null && listenerMap.size() > 0) {
            ArrayList<RopEventListener> ropEventListeners = new ArrayList<RopEventListener>(listenerMap.size());

            //从Spring容器中获取Interceptor
            for (RopEventListenerHodler listenerHolder : listenerMap.values()) {
                ropEventListeners.add(listenerHolder.getRopEventListener());
            }
            return ropEventListeners;
        } else {
            return null;
        }
    }

    public void setFormattingConversionService(FormattingConversionService formattingConversionService) {
        this.formattingConversionService = formattingConversionService;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void setSignEnable(boolean signEnable) {
        this.signEnable = signEnable;
    }

    public void setExtErrorBasename(String extErrorBasename) {
        this.extErrorBasename = extErrorBasename;
    }

    public void setServiceTimeoutSeconds(int serviceTimeoutSeconds) {
        this.serviceTimeoutSeconds = serviceTimeoutSeconds;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setAppSecretManager(AppSecretManager appSecretManager) {
        this.appSecretManager = appSecretManager;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}

