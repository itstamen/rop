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

public class SpringConverter<S, T> implements Converter<S, T> {
	
	@SuppressWarnings("unchecked")
	public static <S, T> Converter<S, T> toSpringConverter(RopConverter<S, T> ropConverter){
		if(ropConverter instanceof Converter){
			return (Converter<S, T>)ropConverter;
		}
		return new SpringConverter<S, T>(ropConverter);
	}

	private final RopConverter<S, T> ropConverter;
	
	public SpringConverter(RopConverter<S, T> ropConverter) {
		super();
		this.ropConverter = ropConverter;
	}
	
	public T convert(S source) {
		return ropConverter.convert(source);
	}
}
