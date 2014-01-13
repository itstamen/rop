/**
 * 日    期：12-3-21
 */
package com.rop;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.rop.utils.RopUtils;

/**
 * Rop框架的异步Servlet总入口
 * 
 * @author angus.aqlu
 * @version 1.0
 */
@WebServlet(asyncSupported = true, urlPatterns = "/action", loadOnStartup = 9)
public class RopAsyncServlet extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ServiceRouter serviceRouter;

    /**
     * 将请求导向到Rop的框架中。
     * 
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext asyncContext = req.startAsync();
        RopUtils.setAsyncContext(asyncContext);
        serviceRouter.service(req, resp);
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        ApplicationContext ctx = getApplicationContext(servletConfig);
        this.serviceRouter = ctx.getBean(ServiceRouter.class);
        if (this.serviceRouter == null) {
            logger.error("在Spring容器中未找到" + ServiceRouter.class.getName()
                    + "的Bean,请在Spring配置文件中通过<aop:annotation-driven/>安装rop框架。");
        }
    }

    private ApplicationContext getApplicationContext(ServletConfig servletConfig) {
        return (ApplicationContext) servletConfig.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

}
