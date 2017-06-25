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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 简单的http请求客户端接口jdk实现
 * 
 * @author liangruisen
 *
 */
public class JdkHttpClient implements HttpClient {

	/**
	 * @see com.rop.client.http.HttpClient#get(java.lang.String)
	 */
	@Override
	public HttpResponse get(String url) throws IOException {
		return execute(null, url, "GET", null);
	}
	
	/**
	 * @see com.rop.client.http.HttpClient#get(java.lang.String, java.util.Map)
	 */
	@Override
	public HttpResponse get(String url, Map<String, String> params) throws IOException {
		return execute(null, url, "GET", params);
	}

	/**
	 * @see com.rop.client.http.HttpClient#get(java.util.Map,
	 * java.lang.String)
	 */
	@Override
	public HttpResponse get(Map<String, String> heads, String url) throws IOException {
		return execute(heads, url, "GET", null);
	}

	/**
	 * @see com.rop.client.http.HttpClient#get(java.util.Map, java.lang.String, java.util.Map)
	 */
	@Override
	public HttpResponse get(Map<String, String> heads, String url, Map<String, String> params) throws IOException {
		return execute(heads, url, "GET", params);
	}

	/**
	 * @see
	 * com.rop.client.http.HttpClient#post(java.lang.String)
	 */
	@Override
	public HttpResponse post(String url) throws IOException {
		return execute(null, url, "POST", null);
	}

	/**
	 * @see com.rop.client.http.HttpClient#post(java.util.Map,
	 * java.lang.String)
	 */
	@Override
	public HttpResponse post(Map<String, String> heads, String url) throws IOException {
		return execute(heads, url, "POST", null);
	}

	/**
	 * @see
	 * com.rop.client.http.HttpClient#post(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public HttpResponse post(String url, Map<String, String> params) throws IOException {
		return execute(null, url, "POST", params);
	}

	/**
	 * @see com.rop.client.http.HttpClient#post(java.util.Map,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public HttpResponse post(Map<String, String> heads, String url, Map<String, String> params) throws IOException {
		return execute(heads, url, "POST", params);
	}

	/**
	 * @see
	 * com.rop.client.http.HttpClient#execute(java.util.Map,
	 * java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public HttpResponse execute(Map<String, String> heads, String url, String method, Map<String, String> params)
			throws IOException {
		JdkHttpRequest request = createRequest(heads, url, method.toUpperCase(Locale.US), params);
		return execute(request);
	}

	private JdkHttpResponse execute(JdkHttpRequest request) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) request.getUrl().openConnection();
		JdkHttpResponse response = new JdkHttpResponse(request, connection);
		Map<String, List<String>> headers = request.getHeaders();
		if (headers != null) {
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				String name = entry.getKey();
				List<String> list = entry.getValue();
				for (String value : list) {
					connection.addRequestProperty(name, value);
				}
			}
		}
		connection.setReadTimeout(1000 * 30);
		connection.setConnectTimeout(1000 * 10);
		connection.setRequestMethod(request.getMethod());
		if (request.getBody() != null) {
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", request.getBody().getType());
			connection.setRequestProperty("Content-Length", String.valueOf(request.getBody().getContentSize()));
		}
		connection.connect();
		if (request.getBody() != null) {
			request.getBody().writeTo(connection.getOutputStream());
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
		}
		return response;
	}

	private JdkHttpRequest createRequest(Map<String, String> heads, String url, String method,
			Map<String, String> params) throws IOException {
		JdkHttpRequest request = new JdkHttpRequest();
		request.setMethod(method);
		if (!isHasBodyMethod(method)) {
			url = appendUrlParams(url, params);
		} else {
			request.setBody(parseParams(params));
		}
		URL urlobj = new URL(url);
		request.setUrl(urlobj);
		addHeaders(request, heads);
		return request;
	}

	/**
	 * 在URL字符串后面拼接请求参数
	 * @param urlstring
	 * @param params
	 * @return String
	 * @throws IOException 
	 */
	public static String appendUrlParams(String urlstring, Map<String, String> params) throws IOException {
		if(params == null || params.isEmpty()){
			return urlstring;
		}
		StringBuilder buf = new StringBuilder(512);
		for(Map.Entry<String, String> entry : params.entrySet()){
			buf.append("&").append(entry.getKey()).append("=");
			buf.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}
		boolean hasParams = urlstring.indexOf("?") > 0;
		if(!hasParams){
			buf.replace(0, 1, "?");
		}
		buf.insert(0, urlstring);
		return buf.toString();
	}
	
	private void addHeaders(JdkHttpRequest request, Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			request.addHeader("Accept-Encoding", "gzip");
			return;
		}
		headers.put("Accept-Encoding", "gzip");
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
	}

	private JdkHttpRequestBody parseParams(Map<String, String> params) throws IOException {
		JdkHttpRequestBody body = new JdkHttpRequestBody();
		if (params == null || params.isEmpty()) {
			return body;
		}
		for (Map.Entry<String, String> entry : params.entrySet()) {
			body.add(entry.getKey(), entry.getValue());
		}
		return body;
	}

	private boolean isHasBodyMethod(String method) {
		return method.equals("POST") || method.equals("PATCH") || method.equals("PUT") || method.equals("DELETE");
	}
}
