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

import java.lang.annotation.*;

/**
 * 默认情况下，请求对象的所有field都会作为请求参数提交，如果希望某个field不作为参数提交，可以打上{@Temporary}注解，如下所示：
 * <pre class="code">
 * <DIV>&nbsp; public class MyRopRequest implements RopRequest{
 * <DIV>&nbsp;</DIV>
 * <DIV>&nbsp; private String field1;</DIV>
 * <DIV>&nbsp;</DIV>
 * <DIV>&nbsp; @Temporary</DIV>
 * <DIV>&nbsp; private String field2;</DIV>&nbsp;}
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Temporary {

}

