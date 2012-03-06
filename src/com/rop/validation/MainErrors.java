/**
 *
 * 日    期：12-2-11
 */
package com.rop.validation;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class MainErrors {


    private static final String ERROR_CODE_PREFIX = "ERROR_";
    private static final String ERROR_SOLUTION_SUBFIX = "_SOLUTION";
    // 错误信息的国际化信息
    private static MessageSourceAccessor errorMessageSourceAccessor;

    public static MainError getError(MainErrorType mainErrorType, Locale locale) {
        String errorMessage = getErrorMessage(ERROR_CODE_PREFIX + mainErrorType.value(), locale);
        String errorSolution = getErrorSolution(ERROR_CODE_PREFIX + mainErrorType.value() + ERROR_SOLUTION_SUBFIX, locale);
        return new SimpleMainError(mainErrorType.value(), errorMessage, errorSolution);
    }

    public static void setErrorMessageSourceAccessor(MessageSourceAccessor errorMessageSourceAccessor) {
        MainErrors.errorMessageSourceAccessor = errorMessageSourceAccessor;
    }

    private static String getErrorMessage(String code, Locale locale) {
        Assert.notNull(errorMessageSourceAccessor, "请先设置错误消息的国际化资源");
        return errorMessageSourceAccessor.getMessage(code, new Object[]{}, locale);
    }

    private static String getErrorSolution(String code, Locale locale) {
        Assert.notNull(errorMessageSourceAccessor, "请先设置错误解决方案的国际化资源");
        return errorMessageSourceAccessor.getMessage(code, new Object[]{}, locale);
    }


}

