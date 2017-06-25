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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author liangruisen
 *
 */
public class JdkHttpRequestBody {

	private String type = "application/x-www-form-urlencoded";
	
	private ByteArrayOutputStream content = new ByteArrayOutputStream();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getContentSize() {
		return content.size();
	}
	
	public void writeTo(OutputStream out) throws IOException{
		content.writeTo(out);
	}
	
	public void add(String name, String value) throws IOException{
		if(content.size() > 0){
			content.write('&');
		}
		content.write(name.getBytes());
		content.write('=');
		content.write(URLEncoder.encode(value, "UTF-8").getBytes());
	}
}
