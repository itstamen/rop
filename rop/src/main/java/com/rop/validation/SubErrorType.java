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
public enum SubErrorType {
    ISP_SERVICE_UNAVAILABLE,
    ISP_REMOTE_SERVICE_ERROR,
    ISP_REMOTE_SERVICE_TIMEOUT,
    ISP_REMOTE_CONNECTION_ERROR,
    ISP_NULL_POINTER_EXCEPTION,
    ISP_BOP_PARSE_ERROR,
    ISP_BOP_REMOTE_CONNECTION_TIMEOUT,
    ISP_BOP_REMOTE_CONNECTION_ERROR,
    ISP_BOP_MAPPING_PARSE_ERROR,
    ISP_UNKNOWN_ERROR,

    ISV_NOT_EXIST,
    ISV_MISSING_PARAMETER,
    ISV_INVALID_PARAMETE,
    ISV_INVALID_PERMISSION,
    ISV_PARAMETERS_MISMATCH;

    private static EnumMap<SubErrorType, String> errorKeyMap = new EnumMap<SubErrorType, String>(SubErrorType.class);

    static {
        errorKeyMap.put(SubErrorType.ISV_NOT_EXIST, "isv.xxx-not-exist:invalid-yyy");
        errorKeyMap.put(SubErrorType.ISV_MISSING_PARAMETER, "isv.missing-parameter:xxx");
        errorKeyMap.put(SubErrorType.ISV_INVALID_PARAMETE, "isv.invalid-paramete:xxx");
        errorKeyMap.put(SubErrorType.ISV_INVALID_PERMISSION, "isv.invalid-permission");
        errorKeyMap.put(SubErrorType.ISV_PARAMETERS_MISMATCH, "isv.parameters-mismatch:xxx-and-yyy");

        errorKeyMap.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, "isp.xxx-service-unavailable");
        errorKeyMap.put(SubErrorType.ISP_REMOTE_SERVICE_ERROR, "isp.remote-service-error");
        errorKeyMap.put(SubErrorType.ISP_REMOTE_SERVICE_TIMEOUT, "isp.remote-service-timeout");
        errorKeyMap.put(SubErrorType.ISP_REMOTE_CONNECTION_ERROR, "isp.remote-connection-error");
        errorKeyMap.put(SubErrorType.ISP_NULL_POINTER_EXCEPTION, "isp.null-pointer-exception");
        errorKeyMap.put(SubErrorType.ISP_BOP_PARSE_ERROR, "isp.bop-parse-error");
        errorKeyMap.put(SubErrorType.ISP_BOP_REMOTE_CONNECTION_TIMEOUT, "isp.bop-remote-connection-timeout");
        errorKeyMap.put(SubErrorType.ISP_BOP_REMOTE_CONNECTION_ERROR, "isp.bop-remote-connection-error");
        errorKeyMap.put(SubErrorType.ISP_BOP_MAPPING_PARSE_ERROR, "isp.bop-mapping-parse-error");
        errorKeyMap.put(SubErrorType.ISP_UNKNOWN_ERROR, "isp.unknown-error");
    }

    public String value() {
        return errorKeyMap.get(this);
    }
}

