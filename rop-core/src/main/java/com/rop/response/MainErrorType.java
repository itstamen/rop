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
public enum MainErrorType {
    SERVICE_CURRENTLY_UNAVAILABLE,
    INSUFFICIENT_ISV_PERMISSIONS,
    INSUFFICIENT_USER_PERMISSIONS,
    UPLOAD_FAIL,
    HTTP_ACTION_NOT_ALLOWED,
    INVALID_ENCODING,
    FORBIDDEN_REQUEST,
    METHOD_OBSOLETED,
    BUSINESS_LOGIC_ERROR,
    MISSING_SESSION,
    INVALID_SESSION,
    MISSING_APP_KEY,
    INVALID_APP_KEY,
    MISSING_SIGNATURE,
    INVALID_SIGNATURE,
    MISSING_METHOD,
    INVALID_METHOD,
    MISSING_VERSION,
    INVALID_VERSION,
    UNSUPPORTED_VERSION,
    INVALID_FORMAT,
    MISSING_REQUIRED_ARGUMENTS,
    INVALID_ARGUMENTS,
    EXCEED_USER_INVOKE_LIMITED,
    EXCEED_SESSION_INVOKE_LIMITED,
    EXCEED_APP_INVOKE_LIMITED,
    EXCEED_APP_INVOKE_FREQUENCY_LIMITED;

    private static EnumMap<MainErrorType, String> errorCodeMap = new EnumMap<MainErrorType, String>(MainErrorType.class);

    static {
        errorCodeMap.put(MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE, "1");
        errorCodeMap.put(MainErrorType.INSUFFICIENT_ISV_PERMISSIONS, "2");
        errorCodeMap.put(MainErrorType.INSUFFICIENT_USER_PERMISSIONS, "3");
        errorCodeMap.put(MainErrorType.UPLOAD_FAIL, "4");
        errorCodeMap.put(MainErrorType.HTTP_ACTION_NOT_ALLOWED, "5");
        errorCodeMap.put(MainErrorType.INVALID_ENCODING, "6");
        errorCodeMap.put(MainErrorType.FORBIDDEN_REQUEST, "7");
        errorCodeMap.put(MainErrorType.METHOD_OBSOLETED, "8");
        errorCodeMap.put(MainErrorType.BUSINESS_LOGIC_ERROR, "9");
        errorCodeMap.put(MainErrorType.MISSING_SESSION, "20");
        errorCodeMap.put(MainErrorType.INVALID_SESSION, "21");
        errorCodeMap.put(MainErrorType.MISSING_APP_KEY, "22");
        errorCodeMap.put(MainErrorType.INVALID_APP_KEY, "23");
        errorCodeMap.put(MainErrorType.MISSING_SIGNATURE, "24");
        errorCodeMap.put(MainErrorType.INVALID_SIGNATURE, "25");
        errorCodeMap.put(MainErrorType.MISSING_METHOD, "26");
        errorCodeMap.put(MainErrorType.INVALID_METHOD, "27");
        errorCodeMap.put(MainErrorType.MISSING_VERSION, "28");
        errorCodeMap.put(MainErrorType.INVALID_VERSION, "29");
        errorCodeMap.put(MainErrorType.UNSUPPORTED_VERSION, "30");
        errorCodeMap.put(MainErrorType.INVALID_FORMAT, "31");
        errorCodeMap.put(MainErrorType.MISSING_REQUIRED_ARGUMENTS, "32");
        errorCodeMap.put(MainErrorType.INVALID_ARGUMENTS, "33");
        errorCodeMap.put(MainErrorType.EXCEED_USER_INVOKE_LIMITED, "34");
        errorCodeMap.put(MainErrorType.EXCEED_SESSION_INVOKE_LIMITED, "35");
        errorCodeMap.put(MainErrorType.EXCEED_APP_INVOKE_LIMITED, "36");
        errorCodeMap.put(MainErrorType.EXCEED_APP_INVOKE_FREQUENCY_LIMITED, "37");
    }

    public String value() {
        return errorCodeMap.get(this);
    }
}

