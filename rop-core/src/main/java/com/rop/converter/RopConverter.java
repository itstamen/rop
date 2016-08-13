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
package com.rop.converter;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopConverter<S, T extends Object> {

	/**
	 * 将S类型转换成T类型
	 * @param source
	 * @return T
	 */
	T convert(S source);
	
    /**
     * 将T类型转换成S类型
     * @param paramValue
     * @return S
     */
    S unconvert(T paramValue);

    /**
     * 获取源类型
     * @return Class<S>
     */
    Class<S> getSourceClass();

    /**
     * 获取目标类型
     * @return Class<T>
     */
    Class<T> getTargetClass();
}

