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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 简单的HTTP请求返回结果接口，用于获取该次请求的结果信息
 * @author liangruisen
 *
 */
public interface HttpResponse extends Closeable {

	/**
	 * 获取实现请求的Response对象
	 * @return Object
	 */
	public Object getResponse();
	
	/**
	 * 获取请求结果的内容字符串
	 * @return String
	 * @throws IOException
	 */
	public String getString() throws IOException;
	
	/**
	 * 获取请求结果的内容字节数组
	 * @return byte[]
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException;
	
	/**
	 * 获取请求结果的内容输入流
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;
	
	/**
	 * 获取请求的HTTP返回状态代码
	 * @return int
	 */
	public int getCode();
	
	/**
	 * 获取实现请求的Request对象
	 * @return Object
	 */
	public Object getRequest();
	
	/**
	 * 获取请求的返回消息
	 * @return String
	 */
	public String getMessage();
	
	/**
	 * 获取HTTP请求的方法名称，
	 * 返回 GET/POST/PUT/HEAD/OPTIONS/PATCH/DELETE/TRACE 中的一个
	 * @return String
	 */
	public String getHeader(String name);
	
	/**
	 * 获取指定名称的HTTP头信息的值
	 * @param name
	 * @return String
	 */
	public List<String> getHeaders(String name);
	
	/**
	 * 获取所有的HTTP头信息
	 * @return Map<String, List<String>>
	 */
	public Map<String, List<String>> getHeaders();
	
	/**
	 * 获取该次请求是否正确返回
	 * @return boolean
	 */
	public boolean isSuccessful();
	
	/**
	 * 获取协议的名称及其版本信息
	 * @return String
	 */
	public String getProtocol();
}
