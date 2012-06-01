/**
 * 日    期：12-3-21
 */
package com.rop;

import com.rop.validation.AppSecretManager;
import com.rop.validation.SessionChecker;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * <pre>
 * 功能说明：ROP的配置对象类
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopConfig {

    private String errorResourceBaseName = "ropError";

    private SessionChecker sessionChecker;

    private AppSecretManager appSecretManager;

    private ApplicationContext applicationContext;

    private List<Interceptor> interceptors;

    private SecurityManager securityManager;
    
    private ServiceMethodContextBuilder serviceMethodContextBuilder;

    private boolean needCheckSign = true;

    public String getErrorResourceBaseName() {
        return errorResourceBaseName;
    }

    public void setErrorBaseName(String errorResourceBaseName) {
        this.errorResourceBaseName = errorResourceBaseName;
    }

    public SessionChecker getSessionChecker() {
        return sessionChecker;
    }

    public void setSessionChecker(SessionChecker sessionChecker) {
        this.sessionChecker = sessionChecker;
    }

    public AppSecretManager getAppSecretManager() {
        return appSecretManager;
    }

    public void setAppSecretManager(AppSecretManager appSecretManager) {
        this.appSecretManager = appSecretManager;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public boolean isNeedCheckSign() {
        return needCheckSign;
    }

    public void setNeedCheckSign(boolean needCheckSign) {
        this.needCheckSign = needCheckSign;
    }

    public ServiceMethodContextBuilder getServiceMethodContextBuilder() {
        return serviceMethodContextBuilder;
    }

    public void setServiceMethodContextBuilder(ServiceMethodContextBuilder serviceMethodContextBuilder) {
        this.serviceMethodContextBuilder = serviceMethodContextBuilder;
    }
}

