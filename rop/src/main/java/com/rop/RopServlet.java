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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;
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

    private static final String ROP_CONFIG_FILE_INITPARAM = "ropConfigLocation";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private RopServiceRouter ropServiceRouter;


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
        RopConfig ropConfig = buildRopConfig(servletConfig);
        ropServiceRouter = new AnnotationRopServiceRouter(ropConfig);
    }

    private RopConfig buildRopConfig(ServletConfig servletConfig) {
        RopConfig ropConfig = new RopConfig();
        ApplicationContext ctx = getApplicationContext(servletConfig);
        if (ctx == null) {
            throw new RopException("请在web.xml中配置好Rop服务类所在的Spring容器");
        } else {
            ropConfig.setApplicationContext(ctx);
        }
        Properties properties = getRopConfigProperties(servletConfig, ctx);

        //优先采用配置文件配置，次之采用Spring容器中的Bean,最后采用默认的
        try {

            //1.装配SessionChecker
            ropConfig.setSessionChecker(initSessionChecker(properties, ctx));

            //2.装配AppSecretManager
            ropConfig.setAppSecretManager(initAppSecretManager(properties, ctx));

            //3.装配SecurityManager
            ropConfig.setSecurityManager(initSecurityManager(properties, ctx));

            //4.注册拦截器
            List<Interceptor> interceptors = initInterceptors(properties, ctx);
            if (interceptors != null && interceptors.size() > 0) {
                sortInteceptors(interceptors);
                ropConfig.setInterceptors(interceptors);
            }

            //5.初始化错误资源
            initErrorResource(properties, ropConfig);

            //6.设置其它参数,
            initOtherParams(properties, ropConfig);


        } catch (Exception e) {
            throw new IllegalArgumentException("启动Rop时发生错误", e);
        }

        return ropConfig;
    }

    private void initOtherParams(Properties properties, RopConfig ropConfig) {

        //初始化needCheckSign参数
        Boolean needCheckSign = Boolean.valueOf(
                properties.getProperty(ConfigPropNames.NEED_CHECK_SIGN_PROPNAME, ConfigPropNames.DEFAULT_NEED_CHECK_SIGN));
        logger.debug("参数ConfigPropNames.NEED_CHECK_SIGN_PROPNAME值为："+needCheckSign);
        ropConfig.setNeedCheckSign(needCheckSign);
    }

    private Properties getRopConfigProperties(ServletConfig servletConfig, ApplicationContext ctx) {
        //默认的配置文件地址
        String ropConfigLocation = "/WEB-INF/" + servletConfig.getServletName() + ".properties";
        if (servletConfig.getInitParameter(ROP_CONFIG_FILE_INITPARAM) != null) {
            ropConfigLocation = servletConfig.getInitParameter(ROP_CONFIG_FILE_INITPARAM);
        }
        Resource resource = ctx.getResource(ropConfigLocation);
        if (!resource.exists()) {
            logger.warn("没有找到Rop的配置文件。默认位于WEB-INF/<ropServletName>.properties，\n" +
                    "你也可以通过RopServlet的ropConfigLocation参数显式指定。");
            return null;
        } else {
            try {
                return PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException e) {
                throw new RopException(e);
            }
        }
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
    private void initErrorResource(Properties properties, RopConfig ropConfig) {
        String errorBaseName = properties.getProperty(ConfigPropNames.ERROR_RESOURCE_BASE_NAME_PROPNAME,
                ConfigPropNames.DEFAULT_ERROR_RESOURCE_BASE_NAME);
        logger.debug("加载应用错误国际化资源:"+errorBaseName);
        ropConfig.setErrorBaseName(errorBaseName);
    }

    /**
     * 初始化拦截器
     *
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @throws ClassNotFoundException
     */
    private List<Interceptor> initInterceptors(Properties properties, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        List<Interceptor> interceptors = getComponent(properties, applicationContext,
                ConfigPropNames.INTERCEPTOR_CLASS_NAMES_PROPNAME, null,
                Interceptor.class, true);
        return interceptors;
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
    private AppSecretManager initAppSecretManager(Properties properties, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        List<AppSecretManager> component = getComponent(properties, applicationContext,
                ConfigPropNames.APP_SECRET_MANAGER_CLASS_NAME_PROPNAME, ConfigPropNames.DEFAULT_APP_SECRET_MANAGER_CLASS_NAME,
                AppSecretManager.class, false);
        Assert.notEmpty(component, "初始化" + AppSecretManager.class.getName() + "组件发生异常");
        return component.get(0);
    }

    /**
     * 初始化{@link SecurityManager}
     *
     * @param servletConfig
     * @param ropConfig
     * @param applicationContext
     * @return
     * @throws ClassNotFoundException
     */
    private SecurityManager initSecurityManager(Properties properties, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        List<SecurityManager> component = getComponent(properties, applicationContext,
                ConfigPropNames.SECURITY_MANAGER_CLASS_NAME_PROPNAME, ConfigPropNames.DEFAULT_SECURITY_MANAGER_CLASS_NAME,
                SecurityManager.class, false);
        Assert.notEmpty(component, "初始化" + SecurityManager.class.getName() + "组件发生异常");
        return component.get(0);
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
    private SessionChecker initSessionChecker(Properties properties, ApplicationContext applicationContext)
            throws ClassNotFoundException {
        List<SessionChecker> component = getComponent(properties, applicationContext,
                ConfigPropNames.SESSION_CHECKER_CLASS_NAME_PROPNAME, ConfigPropNames.DEFAULT_SESSION_CHECKER_CLASS_NAME,
                SessionChecker.class, false);
        Assert.notEmpty(component, "初始化" + SessionChecker.class.getName() + "组件发生异常");
        return component.get(0);
    }

    /**
     * 获取组件对象。如果有web.xml的匹配中通过{@link RopServlet}参数指定相应组件类，则采用该参数指定的组件类进行实例化，否则将采用
     * Spring容器中是否有匹配的组件Bean。如果既没有通过{@link RopServlet}指定组件类，也可以在Spring中提供相应的组件Bean，则采用默认
     * 的组件类<code>defaultComponentClass</code>作为默认的实现。
     *
     * @param servletConfig
     * @param ctx
     * @param paramName
     * @param defaultComponentClassName
     * @param componentClass
     * @param allowMultiple             是否允许多个实例 如拦截器允许多个，而SessionChecker只允许一个
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    private <T> List<T> getComponent(Properties properties, ApplicationContext ctx,
                                     String paramName, String defaultComponentClassName, Class<T> componentClass,
                                     boolean allowMultiple) throws ClassNotFoundException {
        ArrayList<T> components = new ArrayList<T>();
        if (properties == null || !properties.containsKey(paramName)) {//没有配置文件，或者配置文件未定义
            String[] beanNamesForType = ctx.getBeanNamesForType(componentClass);
            if (beanNamesForType != null && beanNamesForType.length > 0) {
                if (allowMultiple) {
                    logger.debug("在Spring容器中找到" + beanNamesForType.length + "个" + componentClass.getName() + "Bean.");
                    components.addAll(ctx.getBeansOfType(componentClass).values());
                } else {
                    if (beanNamesForType.length == 1) {
                        logger.debug("将Spring容器" + componentClass.getName() + "的Bean装配到Rop中.");
                        components.add(ctx.getBean(componentClass));
                    } else {
                        throw new RopException("Spring容器中存在多个" + componentClass.getName() + "组件实例，Rop仅允许一个。");
                    }
                }
            } else {
                if (defaultComponentClassName != null) {
                    logger.debug("使用默认的组件类:" + defaultComponentClassName);
                    components.add(instantiateClassByClassName(defaultComponentClassName, componentClass));
                }
            }
        } else {//配置文件中有指定
            logger.debug("从Rop的配置文件中装载组件：" + componentClass.getName());
            try {
                String componentClassName = properties.getProperty(paramName);
                logger.debug("Rop装配组件：" + componentClassName);
                if (allowMultiple) {
                    String[] componentNames = componentClassName.split(",");
                    for (String componentName : componentNames) {
                        components.add(instantiateClassByClassName(componentName, componentClass));
                    }
                } else {
                    components.add(instantiateClassByClassName(componentClassName, componentClass));
                }
            } catch (ClassNotFoundException e) {
                throw new RopException("初始化组件" + componentClass.getName() + "失败", e);
            }
        }
        return components;
    }

    private ApplicationContext getApplicationContext(ServletConfig servletConfig) {
        return (ApplicationContext) servletConfig.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    private <T> T instantiateClassByClassName(String className, Class<T> clazz) throws ClassNotFoundException {
        Class<?> sessionCheckerClass = ClassUtils.forName(className, this.getClass().getClassLoader());
        return (T) BeanUtils.instantiateClass(sessionCheckerClass);
    }

    private static class ConfigPropNames {

        public static final String SESSION_CHECKER_CLASS_NAME_PROPNAME = "sessionCheckerClassName";

        public static final String DEFAULT_SESSION_CHECKER_CLASS_NAME = "com.rop.validation.DefaultSessionChecker";

        public static final String SECURITY_MANAGER_CLASS_NAME_PROPNAME = "securityManagerClassName";

        public static final String DEFAULT_SECURITY_MANAGER_CLASS_NAME = "com.rop.impl.DefaultSecurityManager";

        public static final String APP_SECRET_MANAGER_CLASS_NAME_PROPNAME = "appSecretManagerClassName";

        public static final String DEFAULT_APP_SECRET_MANAGER_CLASS_NAME = "com.rop.validation.FileBaseAppSecretManager";

        public static final String INTERCEPTOR_CLASS_NAMES_PROPNAME = "interceptorClassNames";

        public static final String NEED_CHECK_SIGN_PROPNAME = "needCheckSign";

        public static final String DEFAULT_NEED_CHECK_SIGN = "true";

        public static final String ERROR_RESOURCE_BASE_NAME_PROPNAME = "errorResourceBaseName";

        public static final String DEFAULT_ERROR_RESOURCE_BASE_NAME = "i18n/rop/ropError";
    }
}

