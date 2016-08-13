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
package com.rop.sample;

import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.sample.request.LogonRequest;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 14-3-6
 */
public interface UserServiceInterface {

    @ServiceMethod(method = "user.getSession",version = "1.0",needInSession = NeedInSessionType.NO)
    Object getSession(LogonRequest request);
}
