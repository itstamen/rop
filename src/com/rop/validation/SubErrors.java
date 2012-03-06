/**
 *
 * 日    期：12-2-11
 */
package com.rop.validation;

import org.springframework.context.support.MessageSourceAccessor;

import java.util.EnumMap;
import java.util.Locale;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SubErrors {

    //子错误和主错误对应Map,key为子错误代码，值为主错误代码
    private static final EnumMap<SubErrorType, MainErrorType> SUBERROR_MAINERROR_MAPPINGS =
            new EnumMap<SubErrorType, MainErrorType>(SubErrorType.class);

    static {
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_SERVICE_UNAVAILABLE, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_REMOTE_SERVICE_ERROR, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_REMOTE_SERVICE_TIMEOUT, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_REMOTE_CONNECTION_ERROR, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_NULL_POINTER_EXCEPTION, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_BOP_PARSE_ERROR, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_BOP_REMOTE_CONNECTION_TIMEOUT, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_BOP_REMOTE_CONNECTION_ERROR, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_BOP_MAPPING_PARSE_ERROR, MainErrorType.INVALID_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISP_UNKNOWN_ERROR, MainErrorType.SERVICE_CURRENTLY_UNAVAILABLE);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_NOT_EXIST, MainErrorType.INVALID_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_MISSING_PARAMETER, MainErrorType.MISSING_REQUIRED_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_INVALID_PARAMETE, MainErrorType.INVALID_ARGUMENTS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_INVALID_PERMISSION, MainErrorType.INSUFFICIENT_USER_PERMISSIONS);
        SUBERROR_MAINERROR_MAPPINGS.put(SubErrorType.ISV_PARAMETERS_MISMATCH, MainErrorType.INVALID_ARGUMENTS);
    }

    private static MessageSourceAccessor messageSourceAccessor;
    private static final String PARAM_1 = "xxx";
    private static final String PARAM_2 = "yyy";

    public static void setErrorMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
        SubErrors.messageSourceAccessor = messageSourceAccessor;
    }

    /**
     * 获取对应子错误的主错误
     *
     * @param subErrorCode
     * @param locale
     * @return
     */
    public static MainError getMainError(SubErrorType subErrorType, Locale locale) {
        return MainErrors.getError(SUBERROR_MAINERROR_MAPPINGS.get(subErrorType), locale);
    }


    /**
     * @param subErrorCode 子错误代码
     * @param subErrorKey  子错误信息键
     * @param locale       本地化
     * @param params       本地化消息参数
     * @return
     */
    public static SubError getSubError(String subErrorCode, String subErrorKey, Locale locale, Object... params) {
        String parsedSubErrorMessage = messageSourceAccessor.getMessage(subErrorKey, params, locale);
        return new SubError(subErrorCode, parsedSubErrorMessage);
    }

    public static String getSubErrorCode(SubErrorType subErrorType, Object... params) {
        String subErrorCode = subErrorType.value();
        if (params.length > 0) {
            if (params.length == 1) {
                subErrorCode = subErrorCode.replace(PARAM_1, (String) params[0]);
            } else {
                subErrorCode = subErrorCode.replace(PARAM_1, (String) params[0]);
                if (params[1] != null) {
                    subErrorCode = subErrorCode.replace(PARAM_2, (String) params[1]);
                }
            }
        }
        return subErrorCode;
    }
}

