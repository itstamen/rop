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

import com.rop.security.SubErrors;

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

    //注意，这个不能删除，否则无法进行流化
    public NotExistErrorResponse() {
    }

    /**
     * 对象不存在的错误对象。当根据<code>queryFieldName</code>查询<code>objectName</code>时，查不到记录，则返回该错误对象。
     *
     * @param objectName     对象的名称
     * @param queryFieldName 查询字段的名称
     * @param locale         本地化对象
     * @param params         错误信息的参数，如错误消息的值为:use '{0}'({1})can't find '{2}' object ，则传入的参数为001时，错误消息格式化为：
     *                       can't find user by 001
     */
    public NotExistErrorResponse(String objectName, String queryFieldName, Object queryFieldValue, Locale locale) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISV_NOT_EXIST, locale);
        String subErrorCode = SubErrors.getSubErrorCode(SubErrorType.ISV_NOT_EXIST, objectName, queryFieldName);

        SubError subError = SubErrors.getSubError(subErrorCode, SubErrorType.ISV_NOT_EXIST.value(), locale,
                                                 queryFieldName, queryFieldValue,objectName);
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }
}

