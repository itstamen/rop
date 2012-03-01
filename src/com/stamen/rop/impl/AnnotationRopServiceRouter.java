/**
 *
 * 日    期：12-2-8
 */
package com.stamen.rop.impl;

import com.stamen.rop.*;
import com.stamen.rop.marshaller.JacksonJsonRopResponseMarshaller;
import com.stamen.rop.marshaller.JaxbXmlRopResponseMarshaller;
import com.stamen.rop.response.ErrorResponse;
import com.stamen.rop.response.ServiceUnavailableErrorResponse;
import com.stamen.rop.validation.MainErrorType;
import com.stamen.rop.validation.MainErrors;
import com.stamen.rop.validation.SubErrors;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Controller
public class AnnotationRopServiceRouter extends ApplicationObjectSupport implements RopServiceRouter {

    //方法的参数名
    private static final String METHOD = "method";

    //格式化参数名
    public static final String MSG_FORMAT = "msgFormat";

    //本地化参数名
    private static final String LOCALE = "locale";

    public static final String SESSION_ID = "sessionId";

    public static final String APP_KEY = "appKey";


    private RopServiceMethodAdapter ropServiceMethodAdapter;

    private RopResponseMarshaller xmlMarshallerRop = new JaxbXmlRopResponseMarshaller();

    private RopResponseMarshaller jsonMarshallerRop = new JacksonJsonRopResponseMarshaller();

    private RopServiceHandlerRegistry ropServiceHandlerRegistry;

    private boolean isInitialized = false;

    @RequestMapping("/router")
    public void service(WebRequest webRequest, HttpServletResponse httpServletResponse) {

        initializeOnlyOnce();

        SimpleRopServiceContext bopServiceContext = buildBopServiceContext(webRequest);

        RopResponse ropResponse = doService(bopServiceContext);

        bopServiceContext.setRopResponse(ropResponse);

        writeResponse(bopServiceContext, httpServletResponse);
    }

    private SimpleRopServiceContext buildBopServiceContext(WebRequest webRequest) {
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

    private ResponseFormat getResponseFormat(WebRequest webRequest) {
        String formatValue = webRequest.getParameter(MSG_FORMAT);
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
                ropResponse = new ServiceUnavailableErrorResponse(context.getMethod(), context.getLocale());
            }
        }
        return ropResponse;
    }

    private Locale getLocale(WebRequest webRequest) {
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
        if (!isInitialized) {
            this.ropServiceHandlerRegistry = new DefaultRopServiceMethodHandlerRegistry(getApplicationContext());
            this.ropServiceMethodAdapter = new AnnotationRopServiceMethodAdapter();
            initMessageSource();
            isInitialized = true;
        }
    }

    /**
     * 设置国际化资源信息
     */
    private void initMessageSource() {
        MainErrors.setErrorMessageSourceAccessor(getMessageSourceAccessor());
        SubErrors.setErrorMessageSourceAccessor(getMessageSourceAccessor());
    }


}

