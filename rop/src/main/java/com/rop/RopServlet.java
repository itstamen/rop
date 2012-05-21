/**
 * 日    期：12-3-21
 */
package com.rop;

import com.rop.impl.AnnotationRopServiceRouter;
import com.rop.validation.AppSecretManager;
import com.rop.validation.SessionChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * <pre>
 *
 * HttpServlet是Rop框架的总入口，提供了多个定制ROP框架的配置参数：
 * 1.Rop会自己扫描Spring容器并加载之{@link SessionChecker}、{@link AppSecretManager}及{@link Interceptor}的Bean。也可以通过
 * "sessionCheckerClassName"、"appSecretManagerClassName"、"interceptorClassNames"的Servlet参数指定实现类的类名。如果显式指定了Servlet
 * 参数，则Rop就不会扫描Spring容器中的Bean了。
 *
 *   如果既没有使用Servlet参数指定，也没有在Spring容器中配置，则Rop使用{@link DefaultSessionChecker}和{@link FileBaseAppSecretManager}
 * 作为{@link SessionChecker}和{@link AppSecretManager}的实现类。
 *
 * 2.可通过"errorResourceBaseName"指定错误资源文件的基名，默认为“i18n/rop/ropError”.
 *
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopServlet extends HttpServlet {

    private static final String SESSION_CHECKER_CLASS_NAME_INITPARAM = "sessionCheckerClassName";

    private static final String DEFAULT_SESSION_CHECKER_CLASS_NAME = "com.rop.validation.DefaultSessionChecker";

    private static final String SECURITY_MANAGER_CLASS_NAME_INITPARAM = "securityManagerClassName";

    private static final String DEFAULT_SECURITY_MANAGER_CLASS_NAME = "com.rop.impl.DefaultSecurityManager";

    private static final String APP_SECRET_MANAGER_CLASS_NAME_INITPARAM = "appSecretManagerClassName";

    private static final String DEFAULT_APP_SECRET_MANAGER_CLASS_NAME = "com.rop.validation.FileBaseAppSecretManager";

    private static final String INTERCEPTOR_CLASS_NAMES_INITPARAM = "interceptorClassNames";

    private static final String NEED_CHECK_SIGN = "needCheckSign";

    private static final String ERROR_RESOURCE_BASE_NAME_INITPARAM = "errorResourceBaseName";


    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private RopServiceRouter ropServiceRouter;

    //错误资源文件的基名
    private String ropErrorBaseName = "i18n/rop/ropError";

    //拦截器的类名，用逗号分隔
    private String interceptorClassNames;

    /**
     * 将请求导向到Rop的框架中。
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ropServiceRouter.service(req, resp);
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        RopConfig ropConfig = new RopConfig();
        ApplicationContext applicationContext = getApplicationContext(servletConfig);
        if (applicationContext == null) {
            throw new RopException("请在web.xml中配置好Rop服务类所在的Spring容器");
        } else {
            ropConfig.setApplicationContext(applicationContext);
        }

        try {

            //1.装配SessionChecker
            ropConfig.setSessionChecker(initSessionChecker(servletConfig, applicationContext));

            //2.装配AppSecretManager
            ropConfig.setAppSecretManager(initAppSecretManager(servletConfig, ropConfig, applicationContext));

            //3.装配SecurityManager
            ropConfig.setSecurityManager(initSecurityManager(servletConfig, ropConfig, applicationContext));

            //4.注册拦截器
            List<Interceptor> interceptors = initInterceptors(servletConfig, applicationContext);
            if(interceptors != null && interceptors.size() > 0){
                sortInteceptors(interceptors);
                ropConfig.setInterceptors(interceptors);
            }

            //5.设置其它参数
            if(servletConfig.getInitParameter(NEED_CHECK_SIGN) != null){
                ropConfig.setNeedCheckSign(Boolean.valueOf(servletConfig.getInitParameter(NEED_CHECK_SIGN)));
            }


        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("初始化Rop组件时发生错误", e);
        }

        //6.初始化错误资源
        initErrorResource(servletConfig, ropConfig);

        ropServiceRouter = new AnnotationRopServiceRouter(ropConfig);
    }

    private void sortInteceptors(List<Interceptor> interceptors) {
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
    }


    /**
     * 初始化错误国际化资源（资源基名），默认在i18n/rop/ropError中。
     *
     * @param servletConfig
     * @param ropConfig
     */
    private void initErrorResource(ServletConfig servletConfig, RopConfig ropConfig) {
        if (servletConfig.getInitParameter(ERROR_RESOURCE_BASE_NAME_INITPARAM) != null) {
            ropErrorBaseName = servletConfig.getInitParameter(ERROR_RESOURCE_BASE_NAME_INITPARAM);
        }
        ropConfig.setErrorBaseName(ropErrorBaseName);
    }

    /**
     * 初始化拦截器
     *
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @throws ClassNotFoundException
     */
    private List<Interceptor> initInterceptors(ServletConfig servletConfig, ApplicationContext applicationContext)
            throws ClassNotFoundException {

        if (servletConfig.getInitParameter(INTERCEPTOR_CLASS_NAMES_INITPARAM) != null) {
            interceptorClassNames = servletConfig.getInitParameter(INTERCEPTOR_CLASS_NAMES_INITPARAM);
            String[] tempClassNames = interceptorClassNames.split(",");
            ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>(tempClassNames.length);
            for (String tempClassName : tempClassNames) {
                interceptors.add(instantiateClassByClassName(tempClassName.trim(), Interceptor.class));
            }
            if (logger.isInfoEnabled()) {
                logger.info("装配" + interceptors.size() + "个通过Servlet参数指定的拦截器.");
            }
            return interceptors;
        } else {
            Map<String, Interceptor> interceptorMap = applicationContext.getBeansOfType(Interceptor.class);
            if (interceptorMap != null && interceptorMap.size() > 0) {
                ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>(interceptorMap.size());
                interceptors.addAll(interceptorMap.values());
                if (logger.isInfoEnabled()) {
                    logger.info("装配" + interceptorMap.size() + "个在Spring容器中定义的拦截器.");
                }
                return interceptors;
            }
        }
        return null;
    }

    /**
     * 初始化{@link AppSecretManager},优先使用{@link #appSecretManagerClassName}参数指定的实现类，否则查找Spring容器中的实现类，
     * 如果都找不到，使用默认的实现类{@link FileBaseAppSecretManager}。
     *
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @throws ClassNotFoundException
     */

    /**
     * 初始化{@link AppSecretManager},优先使用{@link #appSecretManagerClassName}参数指定的实现类，否则查找Spring容器中的实现类，
     * 如果都找不到，使用默认的实现类{@link FileBaseAppSecretManager}。
     *
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @return
     * @throws ClassNotFoundException
     */
    private AppSecretManager initAppSecretManager(ServletConfig servletConfig, RopConfig ropConfig, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        return getComponent(servletConfig, applicationContext,
                APP_SECRET_MANAGER_CLASS_NAME_INITPARAM, DEFAULT_APP_SECRET_MANAGER_CLASS_NAME,
                AppSecretManager.class);
    }

    /**
     * 初始化{@link SecurityManager}
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @return
     * @throws ClassNotFoundException
     */
    private SecurityManager initSecurityManager(ServletConfig servletConfig, RopConfig ropConfig, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        return getComponent(servletConfig, applicationContext,
                SECURITY_MANAGER_CLASS_NAME_INITPARAM, DEFAULT_SECURITY_MANAGER_CLASS_NAME,
                SecurityManager.class);
    }

    /**
     * 初始化{@link SessionChecker}的实例，默认采用{@link #sessionCheckerClassName}指定的实现类，否则查找Spring容器中的匹配Bean,如果都
     * 没有找到，则使用默认的{@link DefaultSessionChecker}
     *
     * @param servletConfig
     * @param applicationContext
     * @return
     * @throws ClassNotFoundException
     */
    private SessionChecker initSessionChecker(ServletConfig servletConfig, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        return getComponent(servletConfig, applicationContext,
                SESSION_CHECKER_CLASS_NAME_INITPARAM, DEFAULT_SESSION_CHECKER_CLASS_NAME,
                SessionChecker.class);
    }

    /**
     * 获取组件对象。如果有web.xml的匹配中通过{@link RopServlet}参数指定相应组件类，则采用该参数指定的组件类进行实例化，否则将采用
     * Spring容器中是否有匹配的组件Bean。如果既没有通过{@link RopServlet}指定组件类，也可以在Spring中提供相应的组件Bean，则采用默认
     * 的组件类<code>defaultComponentClass</code>作为默认的实现。
     *
     * @param servletConfig
     * @param applicationContext
     * @param paramName
     * @param defaultComponentClass
     * @param componentClass
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    private <T> T getComponent(ServletConfig servletConfig, ApplicationContext applicationContext,
                               String paramName, String defaultComponentClass, Class<T> componentClass) throws ClassNotFoundException {
        String componentClassName = null;
        if (servletConfig.getInitParameter(paramName) != null) {
            componentClassName = servletConfig.getInitParameter(paramName);
        }
        if (componentClassName != null) {
            return instantiateClassByClassName(componentClassName, componentClass);
        } else {
            try {
                return applicationContext.getBean(componentClass);
            } catch (BeansException e) {
                String className = componentClass.getName();
                logger.warn("Spring容器中不存在" + className + "的Bean,Rop将使用默认的实现." +
                        "你可以在Spring容器中配置一个" + className + "Bean,也可以通过" +
                        "RopServlet的" + paramName + "初始化参数定义。");
                return instantiateClassByClassName(defaultComponentClass, componentClass);
            }
        }
    }

    private ApplicationContext getApplicationContext(ServletConfig servletConfig) {
        return (ApplicationContext) servletConfig.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    private <T> T instantiateClassByClassName(String className, Class<T> clazz) throws ClassNotFoundException {
        Class<?> sessionCheckerClass = ClassUtils.forName(className, this.getClass().getClassLoader());
        return (T) BeanUtils.instantiateClass(sessionCheckerClass);
    }
}

