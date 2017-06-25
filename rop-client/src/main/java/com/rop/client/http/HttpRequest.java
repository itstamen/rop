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

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 简单的HTTP请求接口，用于获取该次请求的信息
 * @author liangruisen
 *
 */
public interface HttpRequest {

	/**
	 * 获取实现请求的Request对象
	 * @return Object
	 */
	Object getRequest();
	
	/**
	 * 获取该次请求的URI
	 * @return URI
	 */
	URI getURI();
	
	/**
	 * 获取HTTP请求的方法名称，
	 * 返回 GET/POST/PUT/HEAD/OPTIONS/PATCH/DELETE/TRACE 中的一个
	 * @return String
	 */
	String getMethod();
	
	/**
	 * 获取指定名称的HTTP头信息的值
	 * @param name
	 * @return String
	 */
	String getHeader(String name);
	
	/**
	 * 获取获取指定名称的HTTP头信息的值列表
	 * @param name
	 * @return List<String>
	 */
	List<String> getHeaders(String name);
	
	/**
	 * 获取所有的HTTP头信息
	 * @return Map<String, List<String>>
	 */
	Map<String, List<String>> getHeaders();
}
