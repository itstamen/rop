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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General utility methods for working with annotations, handling bridge methods
 * (which the compiler generates for generic declarations) as well as super methods
 * (for optional &quot;annotation inheritance&quot;). Note that none of this is
 * provided by the JDK's introspection facilities themselves.
 *
 * <p>As a general rule for runtime-retained annotations (e.g. for transaction
 * control, authorization, or service exposure), always use the lookup methods
 * on this class (e.g., {@link #findAnnotation(Method, Class)},
 * {@link #getAnnotation(Method, Class)}, and {@link #getAnnotations(Method)})
 * instead of the plain annotation lookup methods in the JDK. You can still
 * explicitly choose between a <em>get</em> lookup on the given class level only
 * ({@link #getAnnotation(Method, Class)}) and a <em>find</em> lookup in the entire
 * inheritance hierarchy of the given method ({@link #findAnnotation(Method, Class)}).
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @author Chris Beams
 * @author Phillip Webb
 * @since 2.0
 * @see java.lang.reflect.Method#getAnnotations()
 * @see java.lang.reflect.Method#getAnnotation(Class)
 */
public abstract class AnnotationUtils {

	private static transient Logger logger;

	/**
	 * Find a single {@link Annotation} of {@code annotationType} on the
	 * supplied {@link Class}, traversing its interfaces, annotations, and
	 * superclasses if the annotation is not <em>present</em> on the given class
	 * itself.
	 * <p>This method explicitly handles class-level annotations which are not
	 * declared as {@link java.lang.annotation.Inherited inherited} <em>as well
	 * as meta-annotations and annotations on interfaces</em>.
	 * <p>The algorithm operates as follows:
	 * <ol>
	 * <li>Search for the annotation on the given class and return it if found.
	 * <li>Recursively search through all interfaces that the given class declares.
	 * <li>Recursively search through all annotations that the given class declares.
	 * <li>Recursively search through the superclass hierarchy of the given class.
	 * </ol>
	 * <p>Note: in this context, the term <em>recursively</em> means that the search
	 * process continues by returning to step #1 with the current interface,
	 * annotation, or superclass as the class to look for annotations on.
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @return the annotation if found, or {@code null} if not found
	 */
	public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
		return findAnnotation(clazz, annotationType, new HashSet<Annotation>());
	}

	/**
	 * Perform the search algorithm for {@link #findAnnotation(Class, Class)},
	 * avoiding endless recursion by tracking which annotations have already
	 * been <em>visited</em>.
	 * @param clazz the class to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @param visited the set of annotations that have already been visited
	 * @return the annotation if found, or {@code null} if not found
	 */
	private static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType, Set<Annotation> visited) {
		Assert.notNull(clazz, "Class must not be null");

		if (isAnnotationDeclaredLocally(annotationType, clazz)) {
			try {
				return clazz.getAnnotation(annotationType);
			}
			catch (Exception ex) {
				// Assuming nested Class values not resolvable within annotation attributes...
				logIntrospectionFailure(clazz, ex);
				return null;
			}
		}

		for (Class<?> ifc : clazz.getInterfaces()) {
			A annotation = findAnnotation(ifc, annotationType, visited);
			if (annotation != null) {
				return annotation;
			}
		}

		try {
			for (Annotation ann : clazz.getDeclaredAnnotations()) {
				if (!isInJavaLangAnnotationPackage(ann) && visited.add(ann)) {
					A annotation = findAnnotation(ann.annotationType(), annotationType, visited);
					if (annotation != null) {
						return annotation;
					}
				}
			}
		}
		catch (Exception ex) {
			// Assuming nested Class values not resolvable within annotation attributes...
			logIntrospectionFailure(clazz, ex);
			return null;
		}

		Class<?> superclass = clazz.getSuperclass();
		if (superclass == null || superclass.equals(Object.class)) {
			return null;
		}
		return findAnnotation(superclass, annotationType, visited);
	}

	/**
	 * Determine whether an annotation for the specified {@code annotationType} is
	 * declared locally on the supplied {@code clazz}. The supplied {@link Class}
	 * may represent any type.
	 * <p>Note: This method does <strong>not</strong> determine if the annotation is
	 * {@linkplain java.lang.annotation.Inherited inherited}. For greater clarity
	 * regarding inherited annotations, consider using
	 * {@link #isAnnotationInherited(Class, Class)} instead.
	 * @param annotationType the Class object corresponding to the annotation type
	 * @param clazz the Class object corresponding to the class on which to check for the annotation
	 * @return {@code true} if an annotation for the specified {@code annotationType}
	 * is declared locally on the supplied {@code clazz}
	 * @see Class#getDeclaredAnnotations()
	 * @see #isAnnotationInherited(Class, Class)
	 */
	public static boolean isAnnotationDeclaredLocally(Class<? extends Annotation> annotationType, Class<?> clazz) {
		Assert.notNull(annotationType, "Annotation type must not be null");
		Assert.notNull(clazz, "Class must not be null");
		boolean declaredLocally = false;
		try {
			for (Annotation ann : clazz.getDeclaredAnnotations()) {
				if (ann.annotationType().equals(annotationType)) {
					declaredLocally = true;
					break;
				}
			}
		}
		catch (Exception ex) {
			// Assuming nested Class values not resolvable within annotation attributes...
			logIntrospectionFailure(clazz, ex);
		}
		return declaredLocally;
	}

	/**
	 * Determine if the supplied {@link Annotation} is defined in the core JDK
	 * {@code java.lang.annotation} package.
	 * @param annotation the annotation to check (never {@code null})
	 * @return {@code true} if the annotation is in the {@code java.lang.annotation} package
	 */
	public static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
		Assert.notNull(annotation, "Annotation must not be null");
		return annotation.annotationType().getName().startsWith("java.lang.annotation");
	}

	private static void logIntrospectionFailure(AnnotatedElement annotatedElement, Exception ex) {
		Logger loggerToUse = logger;
		if (loggerToUse == null) {
			loggerToUse = LoggerFactory.getLogger(AnnotationUtils.class);
			logger = loggerToUse;
		}
		if (loggerToUse.isInfoEnabled()) {
			loggerToUse.info("Failed to introspect annotations on [" + annotatedElement + "]: " + ex);
		}
	}
}

