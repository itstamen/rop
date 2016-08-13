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
package com.rop.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.rop.annotation.IgnoreSign;
import com.rop.annotation.Temporary;
import com.rop.config.SystemParameterNames;

public abstract class ReflectionUtils {

	public static Object getField(Field field, Object target) {
		makeAccessible(field);
		try {
			return field.get(target);
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Make the given field accessible, explicitly setting it accessible if necessary.
	 * The <code>setAccessible(true)</code> method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 * @param field the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers()) ||
				!Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}
	
    public static List<String> getIgnoreSignFieldNames(Class<? extends Object> type){
    	final List<String> list = new ArrayList<String>();
    	list.add(SystemParameterNames.getSign());
    	if(type == null){
    		return list;
    	}
    	doWithFields(type, new FieldCallback() {
            public void doWith(Field field) {
            	list.add(field.getName());
            }
        },
        new FieldFilter() {
            public boolean matches(Field field) {
                //属性类标注了@IgnoreSign
                IgnoreSign typeIgnore = AnnotationUtils.findAnnotation(field.getType(), IgnoreSign.class);
                //属性定义处标注了@IgnoreSign
                IgnoreSign ignoreSign = field.getAnnotation(IgnoreSign.class);
                //属性定义处标注了@Temporary
                Temporary temporary = field.getAnnotation(Temporary.class);
                return typeIgnore != null || ignoreSign != null || temporary != null;
            }
        });
    	return list;
    }
    
    public static List<Field> getFields(Class<? extends Object> type){
    	final List<Field> list = new ArrayList<Field>();
    	if(type == null){
    		return list;
    	}
    	doWithFields(type, new FieldCallback() {
            public void doWith(Field field) {
            	makeAccessible(field);
            	list.add(field);
            }
        },
        new FieldFilter() {
            public boolean matches(Field field) {
                //属性定义处标注了@Temporary
                Temporary temporary = field.getAnnotation(Temporary.class);
                return temporary != null;
            }
        });
    	return list;
    }
    
    public static void doWithFields(Class<?> searchType, FieldCallback callback, FieldFilter filter){
    	doWithFields(searchType, null, callback, filter);
    }
    
    public static void doWithFields(Class<?> searchType, Class<?> stopType, FieldCallback callback, FieldFilter filter){
    	Assert.notNull(searchType, "the searchType argument must be null");
    	Assert.notNull(callback, "the callback argument must be null");
    	if(stopType == null){
    		stopType = Object.class;
    	}
    	while(!stopType.equals(searchType)){
    		Field[] fields = searchType.getDeclaredFields();
    		if(fields != null && fields.length > 0){
    			for(Field field : fields){
    				if(filter != null){
        				if(filter.matches(field)){
        					callback.doWith(field);
        				}
        			}else{
        				callback.doWith(field);
        			}
    			}
    		}
    		searchType = searchType.getSuperclass();
    	}
    }
    
    static interface FieldFilter {
    	boolean matches(Field field);
    }
    
    static interface FieldCallback {
    	void doWith(Field field);
    }
}
