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
public class NotExistErrorResponse extends ErrorResponse {

    public static final String ISV = "isv.";
    public static final String NOT_EXIST_INVALID = "-not-exist:invalid-";

    /**
     * 对象不存在的错误对象。当根据<code>queryFieldName</code>查询<code>objectName</code>时，查不到记录，则返回该错误对象。
     *
     * @param objectName     对象的名称
     * @param queryFieldName 查询字段的名称
     * @param locale         本地化对象
     * @param params         错误信息的参数，如错误消息的值为:can't find user by {0} ，则传入的参数为001时，错误消息格式化为：
     *                       can't find user by 001
     */
    public NotExistErrorResponse(String objectName, String queryFieldName, Object queryFieldValue, Locale locale) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISV_NOT_EXIST, locale);
        String subErrorCode = SubErrors.getSubErrorCode(SubErrorType.ISV_NOT_EXIST, objectName, queryFieldName);


        SubError subError = SubErrors.getSubError(subErrorCode, SubErrorType.ISV_NOT_EXIST.value(), locale, queryFieldName, queryFieldValue);
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }
}

