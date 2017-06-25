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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author liangruisen
 *
 */
public class JdkHttpResponse implements HttpResponse {

	public JdkHttpResponse(JdkHttpRequest request,
			HttpURLConnection connection) {
		super();
		this.request = request;
		this.connection = connection;
	}

	private JdkHttpRequest request;
	
	private HttpURLConnection connection;
	
	/**
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		if(connection != null){
			connection.disconnect();
		}
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getResponse()
	 */
	@Override
	public Object getResponse() {
		return this;
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getString()
	 */
	@Override
	public String getString() throws IOException {
		return IOUtils.toString(getInputStream(), "UTF-8");
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getBytes()
	 */
	@Override
	public byte[] getBytes() throws IOException {
		return IOUtils.toByteArray(getInputStream());
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		String contentEncoding = connection.getContentEncoding();
		if(StringUtils.isNotBlank(contentEncoding) && contentEncoding.toLowerCase(Locale.US).indexOf("gzip") >= 0){
			return new GZIPInputStream(connection.getInputStream());
		}
		return connection.getInputStream();
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getCode()
	 */
	@Override
	public int getCode() {
		try {
			return connection.getResponseCode();
		} catch (IOException e) {
		}
		return -1;
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getRequest()
	 */
	@Override
	public Object getRequest() {
		return request;
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getMessage()
	 */
	@Override
	public String getMessage() {
		try {
			return connection.getResponseMessage();
		} catch (IOException e) {
		}
		return "";
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String name) {
		return connection.getHeaderField(name);
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getHeaders(java.lang.String)
	 */
	@Override
	public List<String> getHeaders(String name) {
		return getHeaders().get(name);
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getHeaders()
	 */
	@Override
	public Map<String, List<String>> getHeaders() {
		return connection.getHeaderFields();
	}

	/**
	 * @see com.rop.client.http.HttpResponse#isSuccessful()
	 */
	@Override
	public boolean isSuccessful() {
		int code = getCode();
		return code >= 200 && code < 300;
	}

	/**
	 * @see com.rop.client.http.HttpResponse#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return request.getUrl().getProtocol();
	}

}
