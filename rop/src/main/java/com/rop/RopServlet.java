/**
 * 日    期：12-3-21
 */
package com.rop;

import com.rop.security.AppSecretManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    protected  Logger logger = LoggerFactory.getLogger(getClass());

    private ServiceRouter serviceRouter;


    /**
     * 将请求导向到Rop的框架中。
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serviceRouter.service(req, resp);
    }


    public void init(ServletConfig servletConfig) throws ServletException {
        ApplicationContext ctx = getApplicationContext(servletConfig);
        this.serviceRouter = ctx.getBean(ServiceRouter.class);
        if (this.serviceRouter == null) {
            logger.error("在Spring容器中未找到" + ServiceRouter.class.getName() +
                    "的Bean,请在Spring配置文件中通过<aop:annotation-driven/>安装rop框架。");
        }
    }

    private ApplicationContext getApplicationContext(ServletConfig servletConfig) {
        return (ApplicationContext) servletConfig.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }
}

