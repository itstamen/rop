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
package com.rop.response;

import java.util.EnumMap;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public enum SubErrorType {
    ISP_SERVICE_UNAVAILABLE,
    ISP_SERVICE_TIMEOUT,

    ISV_NOT_EXIST,
    ISV_INVALID_PERMISSION,
    ISV_MISSING_PARAMETER,
    ISV_INVALID_PARAMETE,
    ISV_PARAMETERS_MISMATCH;

    private static EnumMap<SubErrorType, String> errorKeyMap = new EnumMap<SubErrorType, String>(SubErrorType.class);

    static {
        errorKeyMap.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, "isp.xxx-service-unavailable");
        errorKeyMap.put(SubErrorType.ISP_SERVICE_TIMEOUT, "isp.xxx-service-timeout");

        errorKeyMap.put(SubErrorType.ISV_NOT_EXIST, "isv.xxx-not-exist:invalid-yyy");
        errorKeyMap.put(SubErrorType.ISV_MISSING_PARAMETER, "isv.missing-parameter:xxx");
        errorKeyMap.put(SubErrorType.ISV_INVALID_PARAMETE, "isv.invalid-paramete:xxx");
        errorKeyMap.put(SubErrorType.ISV_INVALID_PERMISSION, "isv.invalid-permission");
        errorKeyMap.put(SubErrorType.ISV_PARAMETERS_MISMATCH, "isv.parameters-mismatch:xxx-and-yyy");
    }

    public String value() {
        return errorKeyMap.get(this);
    }
}

