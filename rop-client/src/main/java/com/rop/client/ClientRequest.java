/*
 * Copyright 2012-2017 the original author or authors.
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
package com.rop.client;

import java.io.IOException;

/**
 * <pre>
 *   每个请求对应一个ClientRequest对象
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface ClientRequest {
	
	/**
	 * 设置http请求头信息
	 * @param name
	 * @param value
	 * @return ClientRequest
	 */
	ClientRequest setHeader(String name, String value);

    /**
     * 添加请求参数,默认需要签名，如果类已经标注了{@link com.rop.annotation.IgnoreSign}则始终不加入签名
     * @param paramName
     * @param paramValue
     * @return ClientRequest
     */
    ClientRequest addParam(String paramName,Object paramValue);

    /**
     * 添加请求参数,如果needSign=false表示不参于签名
     * @param paramName
     * @param paramValue
     * @param needSign
     * @return ClientRequest
     */
    ClientRequest addParam(String paramName,Object paramValue,boolean needSign);

    /**
     * 清除参数列表
     * @return ClientRequest
     */
    ClientRequest clearParam();

    /**
     * 使用POST发起请求
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return CompositeResponse
     */
    <T> CompositeResponse<T> post(Class<T> ropResponseClass, String methodName, String version) throws IOException;

    /**
     * 直接使用 ropRequest发送请求
     * @param ropRequest
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return CompositeResponse
     */
    <T> CompositeResponse<T> post(Object ropRequest, Class<T> ropResponseClass, String methodName, String version) throws IOException;

    /**
     * 使用GET发送服务请求
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return CompositeResponse
     */
    <T> CompositeResponse<T> get(Class<T> ropResponseClass, String methodName, String version) throws IOException;

    /**
     * 使用GET发送ropRequest的请求
     * @param ropRequest
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return CompositeResponse
     */
    <T> CompositeResponse<T> get(Object ropRequest, Class<T> ropResponseClass, String methodName, String version) throws IOException;
}

