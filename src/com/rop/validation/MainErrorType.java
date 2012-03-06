/**
 *
 * 日    期：12-2-23
 */
package com.rop.validation;

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
    APP_CALL_LIMITED,
    HTTP_ACTION_NOT_ALLOWED,
    INVALID_ENCODING,
    FORBIDDEN_REQUEST,
    METHOD_OBSOLETED,
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
    APP_PARAMETER_ERROR,
    MISSING_REQUIRED_ARGUMENTS,
    INVALID_ARGUMENTS,
    UPLOAD_FAIL;

    private static EnumMap<MainErrorType, String> errorCodeMap = new EnumMap<MainErrorType, String>(MainErrorType.class);

    static {
        errorCodeMap.put(MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE, "1");
        errorCodeMap.put(MainErrorType.INSUFFICIENT_ISV_PERMISSIONS, "2");
        errorCodeMap.put(MainErrorType.INSUFFICIENT_USER_PERMISSIONS, "3");
        errorCodeMap.put(MainErrorType.APP_CALL_LIMITED, "4");
        errorCodeMap.put(MainErrorType.HTTP_ACTION_NOT_ALLOWED, "5");
        errorCodeMap.put(MainErrorType.INVALID_ENCODING, "6");
        errorCodeMap.put(MainErrorType.FORBIDDEN_REQUEST, "7");
        errorCodeMap.put(MainErrorType.METHOD_OBSOLETED, "8");
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
        errorCodeMap.put(MainErrorType.APP_PARAMETER_ERROR, "32");
        errorCodeMap.put(MainErrorType.MISSING_REQUIRED_ARGUMENTS, "33");
        errorCodeMap.put(MainErrorType.INVALID_ARGUMENTS, "34");
        errorCodeMap.put(MainErrorType.UPLOAD_FAIL, "35");
    }

    public String value() {
        return errorCodeMap.get(this);
    }
}

