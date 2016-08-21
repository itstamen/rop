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
package com.rop.config;

import java.util.List;

/**
 * <pre>
 *   系统级参数的名称
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SystemParameterNames {

    private static final String JSONP = "callback";

    //方法的默认参数名
    private static final String METHOD = "method";

    //格式化默认参数名
    private static final String FORMAT = "format";

    //本地化默认参数名
    private static final String LOCALE = "locale";

    //会话id默认参数名
    private static final String SESSION_ID = "sessionId";

    //应用键的默认参数名        ;
    private static final String APP_KEY = "appKey";

    //服务版本号的默认参数名
    private static final String VERSION = "v";

    //签名的默认参数名
    private static final String SIGN = "sign";

    private static String method = METHOD;

    private static String format = FORMAT;

    private static String locale = LOCALE;
    private static String sessionId = SESSION_ID;

    private static String appKey = APP_KEY;

    private static String version = VERSION;

    private static String sign = SIGN;

    private static String jsonp = JSONP;
    
    //默认忽略签名的参数列表
    private static List<String> ignoreSignFieldNames;

    public static String getMethod() {
        return method;
    }

    public static void setMethod(String method) {
        SystemParameterNames.method = method;
    }

    public static String getFormat() {
        return format;
    }

    public static void setFormat(String format) {
        SystemParameterNames.format = format;
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        SystemParameterNames.locale = locale;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void setSessionId(String sessionId) {
        SystemParameterNames.sessionId = sessionId;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static void setAppKey(String appKey) {
        SystemParameterNames.appKey = appKey;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        SystemParameterNames.version = version;
    }

    public static String getSign() {
        return sign;
    }

    public static void setSign(String sign) {
        SystemParameterNames.sign = sign;
    }

    public static String getJsonp() {
        return jsonp;
    }

    public static void setJsonp(String jsonp) {
        SystemParameterNames.jsonp = jsonp;
    }

	public static List<String> getIgnoreSignFieldNames() {
		return ignoreSignFieldNames;
	}

	public static void setIgnoreSignFieldNames(List<String> ignoreSignFieldNames) {
		SystemParameterNames.ignoreSignFieldNames = ignoreSignFieldNames;
	}
}

