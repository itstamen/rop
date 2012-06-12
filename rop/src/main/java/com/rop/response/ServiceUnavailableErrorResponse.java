/**
 *
 * 日    期：12-2-23
 */
package com.rop.response;

import com.rop.validation.MainError;
import com.rop.validation.SubError;
import com.rop.validation.SubErrorType;
import com.rop.validation.SubErrors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Locale;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "error")
public class ServiceUnavailableErrorResponse extends ErrorResponse {

    private static final String ISV = "isv.";

    private static final String SERVICE_UNAVAILABLE = "-service-unavailable";

    //注意，这个不能删除，否则无法进行流化
    public ServiceUnavailableErrorResponse() {
    }

    public ServiceUnavailableErrorResponse(String method, Locale locale) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_SERVICE_UNAVAILABLE, locale);
        String errorCodeKey = ISV + transform(method) + SERVICE_UNAVAILABLE;
        SubError subError = SubErrors.getSubError(errorCodeKey, SubErrorType.ISP_SERVICE_UNAVAILABLE.value(), locale, method);
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }

    public ServiceUnavailableErrorResponse(String method, Locale locale,Throwable throwable) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_SERVICE_UNAVAILABLE, locale);
        String errorCodeKey = ISV + transform(method) + SERVICE_UNAVAILABLE;
        SubError subError = SubErrors.getSubError(errorCodeKey, SubErrorType.ISP_SERVICE_UNAVAILABLE.value(), locale, method);
        SubError throwableSubError = new SubError(throwable.getClass().getName(), throwable.getMessage());
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);
        subErrors.add(throwableSubError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }
}

