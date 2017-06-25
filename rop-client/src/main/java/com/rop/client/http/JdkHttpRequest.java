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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author liangruisen
 *
 */
public class JdkHttpRequest implements HttpRequest {

	private String method;
	private URL url;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	
	private JdkHttpRequestBody body;

	/**
	 * @see com.rop.client.http.HttpRequest#getRequest()
	 */
	@Override
	public Object getRequest() {
		return this;
	}

	/**
	 * @see com.rop.client.http.HttpRequest#getURI()
	 */
	@Override
	public URI getURI() {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
		}
		return null;
	}

	/**
	 * @see com.rop.client.http.HttpRequest#getMethod()
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * @see com.rop.client.http.HttpRequest#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String name) {
		List<String> list = getHeaders(name);
		if(list == null || list.isEmpty()){
			return "";
		}
		return StringUtils.join(list, ";");
	}

	/**
	 * @see com.rop.client.http.HttpRequest#getHeaders(java.lang.String)
	 */
	@Override
	public List<String> getHeaders(String name) {
		return headers.get(name);
	}

	/**
	 * @see com.rop.client.http.HttpRequest#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	/**
	 * 设置http请求头信息
	 * @param headers
	 */
	public void setHeaders(Map<String, List<String>> headers){
		if(headers != null){
			this.headers.putAll(headers);
		}
	}
	
	/**
	 * 添加http请求头信息
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value){
		List<String> list = headers.get(name);
		if(list == null){
			list = new ArrayList<String>(1);
			headers.put(name, list);
		}
		list.add(value);
	}

	/**
	 * 设置http请求头信息
	 * @param name
	 * @param value
	 */
	public void setHeader(String name, String value){
		List<String> list = headers.get(name);
		if(list == null){
			list = new ArrayList<String>(1);
			headers.put(name, list);
		}else{
			list.clear();
		}
		list.add(value);
	}
	
	/**
	 * 设置http请求方法
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 获取http请求内容参数
	 * @return JdkRequestBody
	 */
	public JdkHttpRequestBody getBody() {
		return body;
	}

	/**
	 * 设置http请求内容参数
	 * @param body
	 */
	public void setBody(JdkHttpRequestBody body) {
		this.body = body;
	}

	/**
	 * 获取请求的链接
	 * @return
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * 设置请求的链接
	 * @param url
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
}
