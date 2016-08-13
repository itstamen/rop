/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class ServiceMethodHandler {

    //处理器对象
    private Object handler;

    //处理器的处理方法
    private Method handlerMethod;

    private ServiceMethodDefinition serviceMethodDefinition;

    //处理方法的请求对象类
    private Class<? extends Object> requestType = Object.class;

    //无需签名的字段列表
    private List<String> ignoreSignFieldNames;

    //属性类型为FileItem的字段列表
    private List<String> uploadFileFieldNames;

    //是否是实现类
    private boolean ropRequestImplType;

    public ServiceMethodHandler() {
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

    public Class<? extends Object> getRequestType() {
        return requestType;
    }

    public void setRequestType(Class<? extends Object> requestType) {
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

    public ServiceMethodDefinition getServiceMethodDefinition() {
        return serviceMethodDefinition;
    }

    public void setServiceMethodDefinition(ServiceMethodDefinition serviceMethodDefinition) {
        this.serviceMethodDefinition = serviceMethodDefinition;
    }

    public static String methodWithVersion(String methodName, String version) {
        return methodName + "#" + version;
    }

    public boolean isRopRequestImplType() {
        return ropRequestImplType;
    }

    public void setRopRequestImplType(boolean ropRequestImplType) {
        this.ropRequestImplType = ropRequestImplType;
    }

    public List<String> getUploadFileFieldNames() {
        return uploadFileFieldNames;
    }

    public void setUploadFileFieldNames(List<String> uploadFileFieldNames) {
        this.uploadFileFieldNames = uploadFileFieldNames;
    }

    public boolean hasUploadFiles(){
        return uploadFileFieldNames != null && uploadFileFieldNames.size() > 0;
    }
}

