/**
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
package com.rop.client.http;

import java.io.IOException;
import java.util.Map;

/**
 * 简单的http请求客户端接口
 * </p>
 * @author liangruisen
 *
 */
public interface HttpClient {

	/**
	 * 同步发送HTTP GET 请求
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse get(String url) throws IOException;

	/**
	 * 同步发送HTTP GET 请求
	 * @param heads
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse get(String url, Map<String, String> params) throws IOException;
	
	/**
	 * 设置HTTP Header信息并同步发送HTTP GET 请求
	 * @param heads
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse get(Map<String, String> heads, String url) throws IOException;

	/**
	 * 设置HTTP Header信息并同步发送HTTP GET 请求
	 * @param heads
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse get(Map<String, String> heads, String url, Map<String, String> params) throws IOException;
	
	/**
	 * 同步发送HTTP POST 请求
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse post(String url) throws IOException;

	/**
	 * 设置HTTP Header信息并同步发送HTTP POST 请求
	 * @param heads
	 * @param url
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse post(Map<String, String> heads, String url) throws IOException;
	
	/**
	 * 同步发送HTTP POST 请求
	 * @param url
	 * @param params
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse post(String url, Map<String, String> params) throws IOException;
	
	/**
	 * 设置HTTP Header信息并同步发送HTTP POST 请求
	 * @param heads
	 * @param url
	 * @param params
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse post(Map<String, String> heads, String url, Map<String, String> params) throws IOException;
	
	/**
	 * 设置HTTP Header信息并同步发送HTTP 请求
	 * @param heads
	 * @param url
	 * @param method
	 * @param params
	 * @return SimpleResponse
	 * @throws IOException
	 */
	HttpResponse execute(Map<String, String> heads, String url, String method, Map<String, String> params) throws IOException;
}
