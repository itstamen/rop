/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-29
 */
package com.rop.client;

import com.rop.RopRequest;
import com.rop.request.RopConverter;

import java.util.List;
import java.util.Map;


/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopClient {

    /**
     * 添加自定义的转换器
     *
     * @param ropConverter
     */
    void addRopConvertor(RopConverter ropConverter);

    /**
     * 设置method系统参数的参数名，下同
     *
     * @param paramName
     * @return
     */
    RopClient setAppKeyParamName(String paramName);

    /**
     * 设置sessionId的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setSessionIdParamName(String paramName);

    /**
     * 设置method的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setMethodParamName(String paramName);

    /**
     * 设置version的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setVersionParamName(String paramName);

    /**
     * 设置format的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setFormatParamName(String paramName);

    /**
     * 设置locale的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setLocaleParamName(String paramName);

    /**
     * 设置sign的参数名
     *
     * @param paramName
     * @return
     */
    RopClient setSignParamName(String paramName);

    /**
     * 设置sessionId
     *
     * @param sessionId
     */
    void setSessionId(String sessionId);


    /**
     * 如果不需要在会话环境下执行，则使用该方法
     *
     * @param ropRequest
     * @param methodName
     * @param version
     * @return
     */
    <T> CompositeResponse get(RopRequest ropRequest, Class<T> ropResponseClass, String methodName, String version);

    /**
     * 通过参数列表的方式发送服务请求
     * @param businessParams  业务级参数列表
     * @param ropResponseClass  响应的服务类
     * @param methodName 方法名
     * @param version     版本号
     * @param <T>
     * @return
     */
    <T> CompositeResponse get(Map<String, Object> businessParams, Class<T> ropResponseClass, String methodName, String version);

    /**
     * 通过参数列表的方式发送服务请求
     * @param businessParams  业务级参数列表
     * @param ignoreSignParamNames 需要忽略签名的参数列表
     * @param ropResponseClass  响应的服务类
     * @param methodName 方法名
     * @param version     版本号
     * @param <T>
     * @return
     */
    <T> CompositeResponse get(Map<String, Object> businessParams,List<String> ignoreSignParamNames,Class<T> ropResponseClass,
                              String methodName, String version);
    /**
     * 以POST方式发送服务请求
     * @param ropRequest   请求参数对象
     * @param ropResponseClass   响应参数类
     * @param methodName  服务方法
     * @param version
     * @param <T>
     * @return
     */
    <T> CompositeResponse post(RopRequest ropRequest,
                               Class<T> ropResponseClass, String methodName, String version);

    /**
     *以POST方式发送服务请求
     * @param businessParams
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return
     */
    <T> CompositeResponse post(Map<String, Object> businessParams,
                                   Class<T> ropResponseClass, String methodName, String version);

    /**
     *以POST方式发送服务请求
     * @param businessParams
     * @param ignoreSignParamNames
     * @param ropResponseClass
     * @param methodName
     * @param version
     * @param <T>
     * @return
     */
    <T> CompositeResponse post(Map<String, Object> businessParams,List<String> ignoreSignParamNames,
                                       Class<T> ropResponseClass, String methodName, String version);
}

