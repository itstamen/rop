/**
 *
 * 日    期：12-2-10
 */
package com.rop;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <pre>
 *     服务处理器的相关信息
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class RopServiceHandler {

    //处理器对象
    private Object handler;

    //处理器的处理方法
    private Method handlerMethod;

    //处理方法的请求对象类
    private Class<? extends RopRequest> requestType = RopRequest.class;

    //处理方法是否需要在会话环境下运行
    private boolean needInSession;

    //无需签名的字段列表
    private List<String> ignoreSignFieldNames;

    public RopServiceHandler() {
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public Class<? extends RopRequest> getRequestType() {
        return requestType;
    }

    public void setRequestType(Class<? extends RopRequest> requestType) {
        this.requestType = requestType;
    }


    public boolean isHandlerMethodWithParameter() {
        return this.requestType != null;
    }

    public void setIgnoreSignFieldNames(List<String> ignoreSignFieldNames) {
        this.ignoreSignFieldNames = ignoreSignFieldNames;
    }

    public List<String> getIgnoreSignFieldNames() {
        return ignoreSignFieldNames;
    }

    public boolean isNeedInSession() {
        return needInSession;
    }

    public void setNeedInSession(boolean needInSession) {
        this.needInSession = needInSession;
    }
}

