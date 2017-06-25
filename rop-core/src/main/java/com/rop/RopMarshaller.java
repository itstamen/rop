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
package com.rop;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <pre>
 *   负责将请求方法返回的响应对应流化为相应格式的内容。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopMarshaller {
    void marshaller(Object object, OutputStream outputStream) throws IOException;
}

