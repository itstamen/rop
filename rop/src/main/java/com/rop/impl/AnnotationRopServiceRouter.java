/**
 *
 * 日    期：12-2-8
 */
package com.rop.impl;

import com.rop.*;
import com.rop.marshaller.JacksonJsonRopResponseMarshaller;
import com.rop.marshaller.JaxbXmlRopResponseMarshaller;
import com.rop.response.ErrorResponse;
import com.rop.response.ServiceUnavailableErrorResponse;
import com.rop.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class AnnotationRopServiceRouter implements RopServiceRouter {

    //方法的参数名
    private static final String METHOD = "method";

    //格式化参数名
    public static final String FORMAT = "format";

    //本地化参数名
    private static final String LOCALE = "locale";

    public static final String SESSION_ID = "sessionId";

    public static final String APP_KEY = "appKey";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_ROP_ERROR = "ropError";

    private static final String I18N_ROP_ERROR = "i18n/rop/error";

    private RopConfig ropConfig;

    private RopServiceMethodAdapter ropServiceMethodAdapter;

    private RopResponseMarshaller xmlMarshallerRop = new JaxbXmlRopResponseMarshaller();

    private RopResponseMarshaller jsonMarshallerRop = new JacksonJsonRopResponseMarshaller();

    private RopServiceHandlerRegistry ropServiceHandlerRegistry;

    private boolean isInitialized = false;

    public AnnotationRopServiceRouter() {
        ropConfig = new RopConfig();
        ropConfig.setAppSecretManager(new FileBaseAppSecretManager());
        ropConfig.setSessionChecker(new DefaultSessionChecker());
        ropConfig.setErrorBaseName(DEFAULT_ROP_ERROR);
    }

    public AnnotationRopServiceRouter(RopConfig ropConfig) {
        this.ropConfig = ropConfig;
    }

    public void service(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        initializeOnlyOnce();

        SimpleRopServiceContext bopServiceContext = buildBopServiceContext(request);

        RopResponse ropResponse = doService(bopServiceContext);

        bopServiceContext.setRopResponse(ropResponse);

        writeResponse(bopServiceContext, httpServletResponse);
    }

    private SimpleRopServiceContext buildBopServiceContext(HttpServletRequest webRequest) {
        SimpleRopServiceContext bopServiceContext = new SimpleRopServiceContext();
        bopServiceContext.setMethod(webRequest.getParameter(METHOD));
        bopServiceContext.setRopServiceHandler(ropServiceHandlerRegistry.getBopServiceHandler(webRequest.getParameter(METHOD)));
        bopServiceContext.setLocale(getLocale(webRequest));
        bopServiceContext.setResponseFormat(getResponseFormat(webRequest));
        bopServiceContext.setWebRequest(webRequest);
        bopServiceContext.setSessionId(webRequest.getParameter(SESSION_ID));
        bopServiceContext.setAppKey(webRequest.getParameter(APP_KEY));
        return bopServiceContext;
    }

    private ResponseFormat getResponseFormat(HttpServletRequest webRequest) {
        String formatValue = webRequest.getParameter(FORMAT);
        return ResponseFormat.getFormat(formatValue);
    }

    private void writeResponse(RopServiceContext ropServiceContext, HttpServletResponse httpServletResponse) {
        try {
            httpServletResponse.setCharacterEncoding("UTF-8");
            if (ropServiceContext.getResponseFormat() == ResponseFormat.XML) {
                httpServletResponse.setContentType("application/xml");
                xmlMarshallerRop.marshaller(ropServiceContext.getRopResponse(), httpServletResponse.getOutputStream());
            } else {
                httpServletResponse.setContentType("application/json");
                jsonMarshallerRop.marshaller(ropServiceContext.getRopResponse(), httpServletResponse.getOutputStream());
            }
        } catch (IOException e) {
            throw new RopException(e);
        }
    }

    private RopResponse doService(RopServiceContext context) {
        RopResponse ropResponse = null;
        if (context.getMethod() == null) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.MISSING_METHOD, context.getLocale()));
        } else if (!ropServiceHandlerRegistry.isValidMethod(context.getMethod())) {
            ropResponse = new ErrorResponse(MainErrors.getError(MainErrorType.INVALID_METHOD, context.getLocale()));
        } else {
            try {
                ropResponse = ropServiceMethodAdapter.invokeServiceMethod(context);
            } catch (Exception e) { //出错则招聘服务不可用的异常
                if (logger.isInfoEnabled()) {
                    logger.info("调用"+context.getMethod()+"时发生异常，异常信息为："+e.getMessage());
                }
                ropResponse = new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale());
            }
        }
        return ropResponse;
    }

    private Locale getLocale(HttpServletRequest webRequest) {
        if (webRequest.getParameter(LOCALE) != null) {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setAsText(webRequest.getParameter(LOCALE));
            return (Locale) localeEditor.getValue();
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }

    /**
     * 在WebApplication初始化完成后执行，完成BOP的初始化工作
     *
     * @param event
     */
    public void initializeOnlyOnce() {
        if (logger.isInfoEnabled()) {
            logger.info("扫描并注册ROP的服务方法");
        }
        if (!isInitialized) {
            this.ropServiceHandlerRegistry = new DefaultRopServiceMethodHandlerRegistry(ropConfig.getApplicationContext());
            this.ropServiceMethodAdapter = new AnnotationRopServiceMethodAdapter();
            initMessageSource();
            isInitialized = true;
        }
    }

    /**
     * 设置国际化资源信息
     */
    private void initMessageSource() {
        if (logger.isDebugEnabled()) {
            logger.debug("注册错误国际化资源：" + I18N_ROP_ERROR + "," + ropConfig.getErrorResourceBaseName());
        }
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setBasenames(I18N_ROP_ERROR, ropConfig.getErrorResourceBaseName());
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(bundleMessageSource);
        MainErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
        SubErrors.setErrorMessageSourceAccessor(messageSourceAccessor);
    }


}

