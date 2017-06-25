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
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

    private static final String ISP = "isp.";

    private static final String SERVICE_UNAVAILABLE = "-service-unavailable";

    //注意，这个不能删除，否则无法进行流化
    public ServiceUnavailableErrorResponse() {
    }

    public ServiceUnavailableErrorResponse(String method, Locale locale) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_SERVICE_UNAVAILABLE, locale);
        String errorCodeKey = ISP + transform(method) + SERVICE_UNAVAILABLE;
        SubError subError = SubErrors.getSubError(errorCodeKey,
                SubErrorType.ISP_SERVICE_UNAVAILABLE.value(),
                locale, method,"NONE","NONE");
        ArrayList<SubError> subErrors = new ArrayList<SubError>();
        subErrors.add(subError);

        setMainError(mainError);
        setSubErrors(subErrors);
    }

    public ServiceUnavailableErrorResponse(String method, Locale locale, Throwable throwable) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_SERVICE_UNAVAILABLE, locale);

        ArrayList<SubError> subErrors = new ArrayList<SubError>();

        String errorCodeKey = ISP + transform(method) + SERVICE_UNAVAILABLE;
        Throwable srcThrowable = throwable;
        if(throwable.getCause() != null){
            srcThrowable = throwable.getCause();
        }
        SubError subError = SubErrors.getSubError(errorCodeKey,
                SubErrorType.ISP_SERVICE_UNAVAILABLE.value(),
                locale,
                method, srcThrowable.getClass().getName(),getThrowableInfo(throwable));
        subErrors.add(subError);

        setSubErrors(subErrors);
        setMainError(mainError);
    }

    private String getThrowableInfo(Throwable throwable) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        PrintStream printStream = new PrintStream(outputStream);
        throwable.printStackTrace(printStream);
        return outputStream.toString();
    }
}

