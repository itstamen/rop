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
var rop = {};
(function(r, $) {
	var defaultConfig = {
		serverUrl: "",
		appKey: "",
		appSecret: "",
		sessionId: "",
		locale: "zh_CN",
		format: "json",
		version: "1.0",
		methodName: "method",
		formatName: "format",
		versionName: "v",
		signName: "sign",
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
	/**
	 * 审核数据是否是空白的字符串
	 */
	function assertNotBlank(data, message) {
		if(typeof data === 'undefined' || "" === $.trim(data)) {
			message = message || "the arguments cannot be blank.";
			throw new Error(message);
		}
	}
	/**
	 * 获取需要忽略签名的参数名称
	 */
	function getIgnoreNames(options){
		var ignoreNames = options.ignoreNames || [options.jsonpName,options.signName];
		return ignoreNames;
	}
	/**
	 * 判断对象是否在列表里面
	 */
	function contains(a, obj) {
	    for (var i = 0; i < a.length; i++) {
	        if (a[i] === obj) {
	            return true;
	        }
	    }
	    return false;
	}
	/**
	 * 数据过滤验证函数
	 */
	function dataFilter(method, data, options, ignoreNames) {
		var data = data || {};
		var ignoreNames = ignoreNames || getIgnoreNames(options);
		assertNotBlank(options.serverUrl, "the arguments rop.config.serverUrl cannot be blank.");
		var appKey = data[options.appKeyName] || options.appKey;
		assertNotBlank(appKey, "the arguments appKey cannot be blank.");
		data[options.appKeyName] = appKey;
		var method = method || data[options.methodName];
		assertNotBlank(method, "the arguments method cannot be blank.");
		data[options.methodName] = method;
		var locale = data[options.localeName] || options.locale;
		data[options.localeName] = locale;
		var format = data[options.formatName] || options.format;
		data[options.formatName] = format;
		var sessionId = data[options.sessionIdName] || options.sessionId;
		data[options.sessionIdName] = sessionId;
		var version = data[options.versionName] || options.version;
		data[options.versionName] = version;
		if(options.enableSign && options.enableSign == true){
			if(typeof(options.signCallback) === "function"){
				data[options.signName] = options.signCallback.call(this, data, ignoreNames);
			}else{
				var paramNames = new Array();
				for(var name in data) {
					if(!contains(ignoreNames, name) && options.signName !== name) {
						paramNames.push(name);
					}
				}
				paramNames.sort();
				var val = options.appSecret;
				for(var k in paramNames) {
					val += paramNames[k] + data[paramNames[k]];
				}
				val += options.appSecret;
				var hash = "";
				if(typeof(options.hexSha1Callback) === "function"){
					hash = options.hexSha1Callback.call(val);
				}else{
					hash = $.encoding.digests.hexSha1Str(val);
				}
				data[options.signName] = hash;
			}
		}
		return data;
	}
	
	/**
	 * 通用的异步调用服务接口请求函数
	 */
	function ajaxComm(type, method, opt, callback){
		var opts = opt;
		var callFn = callback;
		if(typeof(method) === "object") {
			opts = method;
		}else if(typeof(method) === "function") {
			callFn = callback || method;
		}
		if(typeof(opt) === "function") {
			callFn = callback || opt;
		}
		var options = $.extend(defaultConfig, r.config, opts);
		var data = dataFilter(method, options.data, options);
		var dataType = data[options.jsonpName] ? "jsonp" : data[options.formatName];
		var type = "POST" === type ? "POST" : "GET";
		$.ajax(options.serverUrl,{error:function(jqXHR, textStatus, errorThrown){
			if(typeof(options.errorCallback) === "function"){
				options.errorCallback.call(this, jqXHR, textStatus, errorThrown);
			}
		},success:function(data, textStatus, jqXHR){
			if(data.errorToken){
				if(typeof(options.serverError) === "function"){
					options.serverError.call(this, data, textStatus, jqXHR);
				}else{
					console.log(data.message);
				}
			}else if(typeof(callFn) === "function"){
				callFn.call(this, data, textStatus, jqXHR);
			}
		},data:data,dataType:dataType,type:type});
	}
	/**
	 * get方式调用服务方法
	 * @param method 方法名称
	 * @param options 参数
	 * @param callback 回调函数
	 */
	get=function(method, options, callback){
		ajaxComm("GET", method, options, callback);
	};
	/**
	 * post方式调用服务方法
	 * @param method 方法名称
	 * @param options 参数
	 * @param callback 回调函数
	 */
	post = function(method, options, callback) {
		ajaxComm("POST", method, options, callback);
	};
	r.get = get;
	r.post = post;
	r.config = {};
})(rop, $);