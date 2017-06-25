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
package com.rop.sign;

import java.util.Collection;
import java.util.Map;

/**
 * 数字签名处理接口
 * @author liangruisen
 */
public interface SignHandler {

	/**
	 * 产生签名
	 * @param paramMap
	 * @return 签名字符串
	 */
	String sign(Map<String, String> paramMap);
	
	/**
	 * 产生签名
	 * @param paramMap
	 * @return 签名字符串
	 */
	String sign(Map<String, String> paramMap, Collection<String> ignore);
	
	/**
	 * 检查签名是否正确
	 * @param sign
	 * @param paramMap
	 * @param ignore
	 * @return boolean
	 */
	boolean signCheck(String sign, Map<String, String> paramMap, Collection<String> ignore);
	
	/**
	 * 检查签名是否正确
	 * @param sign
	 * @param paramMap
	 * @return boolean
	 */
	boolean signCheck(String sign, Map<String, String> paramMap);
}
