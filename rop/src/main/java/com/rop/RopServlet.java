/**
 * 日    期：12-3-21
 */
package com.rop;

import com.rop.impl.AnnotationRopServiceRouter;
import com.rop.validation.AppSecretManager;
import com.rop.validation.SessionChecker;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopServlet extends HttpServlet {

    private RopServiceRouter ropServiceRouter;

    //错误资源文件的基名
    private String ropErrorBaseName = "i18n/rop/ropError";

    //会话检查的实现类
    private String sessionCheckerImpl = "com.rop.validation.DefaultSessionChecker";

    //应用密钥管理器的实现类
    private String appSecretManagerImpl = "com.rop.validation.FileBaseAppSecretManager";

    @Override
    public void init(ServletConfig config) throws ServletException {
        RopConfig ropConfig = new RopConfig();

        Object obj = config.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if(obj == null){
            throw new RopException("请在web.xml中配置好Spring上下文。");
        }else {
            ropConfig.setApplicationContext((ApplicationContext)obj);
        }

        if (config.getInitParameter("sessionCheckerImpl") != null) {
            sessionCheckerImpl = config.getInitParameter("sessionCheckerImpl");
        }
        if (config.getInitParameter("appSecretManagerImpl") != null) {
            appSecretManagerImpl = config.getInitParameter("appSecretManagerImpl");
        }
        try {
            Class<?> sessionCheckerClass = ClassUtils.forName(sessionCheckerImpl, this.getClass().getClassLoader());
            ropConfig.setSessionChecker((SessionChecker) BeanUtils.instantiateClass(sessionCheckerClass));
            Class<?> appSecretManagerImplClass = ClassUtils.forName(appSecretManagerImpl, this.getClass().getClassLoader());
            ropConfig.setAppSecretManager((AppSecretManager) BeanUtils.instantiateClass(appSecretManagerImplClass));
        } catch (ClassNotFoundException e) {
            throw new RopException("请检查根类路径或com/stamen/rop的类路径的rop.properties中的配置", e);
        }

        if (config.getInitParameter("ropErrorBaseName") != null) {
            ropErrorBaseName = config.getInitParameter("ropErrorBaseName");
        }
        ropConfig.setErrorBaseName(ropErrorBaseName);
        ropServiceRouter = new AnnotationRopServiceRouter(ropConfig);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ropServiceRouter.service(req, resp);
    }

}

