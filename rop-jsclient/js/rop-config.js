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
rop.config = {
	serverUrl : "", // 服务器请求url
	appKey : "",
	appSecret : "",
	sessionId : "",
	locale : "zh_CN",
	format : "json",
	version : "1.0",
	methodName : "method",
	formatName : "format",
	versionName : "v",
	signName : "sign",
	jsonpName : "callback", // jsonp参数名称
	appKeyName : "appKey", // appKey参数名称
	sessionIdName : "sessionId", // sessionId参数名称
	localeName : "locale", // 语言
	enableSign : true, // 是否启用参数签名
	errorCallback : null, // 错误回调函数
	serverError : null, // 服务错误的回调函数
	hexSha1Callback : null, // sha1 checksum函数
	ignoreNames : null, // 忽略签名的参数名称列表
	signCallback : null // 签名函数
};