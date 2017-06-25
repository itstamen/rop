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

import org.springframework.core.convert.converter.Converter;

/**
 * Converter<Object, Object>不使用泛型是为了解决spring4.x版本验证Converter实现类没有设置泛型的具体类型的检查不通过的问题
 */
@SuppressWarnings("rawtypes")
public class SpringConverter implements Converter<Object, Object> {
	
	public static Converter<?, ?> toSpringConverter(RopConverter<?, ?> ropConverter){
		if(ropConverter instanceof Converter){
			return (Converter<?, ?>)ropConverter;
		}
		return new SpringConverter(ropConverter);
	}

	private final RopConverter ropConverter;
	
	public SpringConverter(RopConverter ropConverter) {
		super();
		this.ropConverter = ropConverter;
	}
	
	@SuppressWarnings("unchecked")
	public Object convert(Object source) {
		return ropConverter.convert(source);
	}
}
