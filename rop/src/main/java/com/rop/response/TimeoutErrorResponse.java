/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-5
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
public class TimeoutErrorResponse extends ErrorResponse {

    public TimeoutErrorResponse() {
    }

    public TimeoutErrorResponse( Locale locale) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_REMOTE_SERVICE_TIMEOUT, locale);
        String subErrorCode = SubErrors.getSubErrorCode(SubErrorType.ISP_REMOTE_SERVICE_TIMEOUT);


        SubError subError = SubErrors.getSubError(subErrorCode, SubErrorType.ISP_REMOTE_SERVICE_TIMEOUT.value(),locale);
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }
}

