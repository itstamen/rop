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
public class TimeoutErrorResponse extends ErrorResponse {

    private static final String ISP = "isp.";

    private static final String SERVICE_TIMEOUT = "-service-timeout";

    public TimeoutErrorResponse() {
    }

    public TimeoutErrorResponse(String method, Locale locale, int timeout) {
        MainError mainError = SubErrors.getMainError(SubErrorType.ISP_SERVICE_TIMEOUT, locale);

        ArrayList<SubError> subErrors = new ArrayList<SubError>();

        String errorCodeKey = ISP + transform(method) + SERVICE_TIMEOUT;
        SubError subError = SubErrors.getSubError(errorCodeKey,
                SubErrorType.ISP_SERVICE_TIMEOUT.value(),
                locale,
                method, timeout);
        subErrors.add(subError);

        setSubErrors(subErrors);
        setMainError(mainError);
    }

}

