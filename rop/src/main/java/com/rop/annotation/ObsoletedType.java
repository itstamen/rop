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
package com.rop.annotation;

/**
 * <pre>
 *   服务方法是否已经过期，过期的服务方法不能再访问
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public enum  ObsoletedType {
    YES, NO, DEFAULT;

     public static boolean isObsoleted(ObsoletedType type) {
         if (YES == type ) {
             return true;
         } else {
             return false;
         }
     }
}

