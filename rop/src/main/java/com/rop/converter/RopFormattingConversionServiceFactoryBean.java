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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringValueResolver;

/**
 * 该类解决spring将RopConverter转换成org.springframework.core.convert.converter.Converter
 * 来解决设置的RopConverter对象没有实现org.springframework.core.convert.converter.Converter接口的问题
 */
public class RopFormattingConversionServiceFactoryBean implements FactoryBean<FormattingConversionService>, EmbeddedValueResolverAware, InitializingBean{

	private Set<?> converters;

	private Set<?> formatters;

	private Set<FormatterRegistrar> formatterRegistrars;

	private boolean registerDefaultFormatters = true;

	private StringValueResolver embeddedValueResolver;

	private FormattingConversionService conversionService;

	/**
	 * Configure the set of custom converter objects that should be added.
	 * @param converters instances of any of the following:
	 * {@link org.springframework.core.convert.converter.Converter},
	 * {@link org.springframework.core.convert.converter.ConverterFactory},
	 * {@link org.springframework.core.convert.converter.GenericConverter}
	 */
	public void setConverters(Set<?> converters) {
		this.converters = converters;
	}

	/**
	 * Configure the set of custom formatter objects that should be added.
	 * @param formatters instances of {@link Formatter} or {@link AnnotationFormatterFactory}
	 */
	public void setFormatters(Set<?> formatters) {
		this.formatters = formatters;
	}

	/**
	 * <p>Configure the set of FormatterRegistrars to invoke to register
	 * Converters and Formatters in addition to those added declaratively
	 * via {@link #setConverters(Set)} and {@link #setFormatters(Set)}.
	 * <p>FormatterRegistrars are useful when registering multiple related
	 * converters and formatters for a formatting category, such as Date
	 * formatting. All types related needed to support the formatting
	 * category can be registered from one place.
	 * <p>FormatterRegistrars can also be used to register Formatters
	 * indexed under a specific field type different from its own &lt;T&gt;,
	 * or when registering a Formatter from a Printer/Parser pair.
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Formatter)
	 * @see FormatterRegistry#addFormatterForFieldType(Class, Printer, Parser)
	 */
	public void setFormatterRegistrars(Set<FormatterRegistrar> formatterRegistrars) {
		this.formatterRegistrars = formatterRegistrars;
	}

	/**
	 * Indicate whether default formatters should be registered or not.
	 * <p>By default, built-in formatters are registered. This flag can be used
	 * to turn that off and rely on explicitly registered formatters only.
	 * @see #setFormatters(Set)
	 * @see #setFormatterRegistrars(Set)
	 */
	public void setRegisterDefaultFormatters(boolean registerDefaultFormatters) {
		this.registerDefaultFormatters = registerDefaultFormatters;
	}

	public void setEmbeddedValueResolver(StringValueResolver embeddedValueResolver) {
		this.embeddedValueResolver = embeddedValueResolver;
	}

	public void afterPropertiesSet() {
		this.conversionService = new DefaultFormattingConversionService(this.embeddedValueResolver, this.registerDefaultFormatters);
		ConversionServiceFactory.registerConverters(parseConverters(this.converters), this.conversionService);
		registerFormatters();
	}
	
	private Set<?> parseConverters(Set<?> converters){
		if(converters == null || converters.isEmpty()){
			return converters;
		}
		Set<Object> set = new HashSet<Object>(converters.size());
		for(Object converter : converters){
			if(converter instanceof RopConverter){
				converter = SpringConverter.toSpringConverter((RopConverter<?, ?>)converter);
			}
			set.add(converter);
		}
		return set;
	}

	private void registerFormatters() {
		if (this.formatters != null) {
			for (Object formatter : this.formatters) {
				if (formatter instanceof Formatter<?>) {
					this.conversionService.addFormatter((Formatter<?>) formatter);
				}
				else if (formatter instanceof AnnotationFormatterFactory<?>) {
					this.conversionService.addFormatterForFieldAnnotation((AnnotationFormatterFactory<?>) formatter);
				}
				else {
					throw new IllegalArgumentException(
							"Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
				}
			}
		}
		if (this.formatterRegistrars != null) {
			for (FormatterRegistrar registrar : this.formatterRegistrars) {
				registrar.registerFormatters(this.conversionService);
			}
		}
	}

	public FormattingConversionService getObject() {
		return this.conversionService;
	}

	public Class<? extends FormattingConversionService> getObjectType() {
		return FormattingConversionService.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
