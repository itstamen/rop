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
		jsonpName: "callback",
		appKeyName: "appKey",
		sessionIdName: "sessionId",
		localeName: "locale",
		errorCallback: null,
		serverError:null,
		hexSha1Str:null,
		ignoreNames:null
	};
	function assertNotBlank(data, message) {
		if(typeof data === 'undefined' || "" === $.trim(data)) {
			message = message || "the arguments cannot be blank.";
			throw new Error(message);
		}
	}
	function getIgnoreNames(options){
		var ignoreNames = options.ignoreNames || [options.jsonpName,options.signName];
		return ignoreNames;
	}
	function contains(a, obj) {
	    for (var i = 0; i < a.length; i++) {
	        if (a[i] === obj) {
	            return true;
	        }
	    }
	    return false;
	}
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
		if(typeof(options.hexSha1Str) === "function"){
			hash = options.hexSha1Str.call(val);
		}else{
			hash = $.encoding.digests.hexSha1Str(val);
		}
		data[options.signName] = hash;
		return data;
	}
	function ajaxComm(type, method, data, callback){
		var d = data;
		var callFn = callback;
		if(typeof(method) === "object") {
			d = method;
		}
		if(typeof(method) === "function") {
			callFn = callback || method;
		}
		if(typeof(data) === "function") {
			callFn = callback || data;
		}
		var options = $.extend(defaultConfig, r.config);
		var data = dataFilter(method, d, options);
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
	get=function(method, data, callback){
		ajaxComm("GET", method, data, callback);
	};
	post = function(method, data, callback) {
		ajaxComm("POST", method, data, callback);
	};
	r.get = get;
	r.post = post;
	r.config = {};
})(rop, $);