/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-1
 */
package com.rop;

import org.springframework.format.support.FormattingConversionService;

/**
 * <pre>
 *    更改请求对象创建{@link RequestContext}实例,子类可以根据多种传输协议定义自己的创建器。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RequestContextBuilder {

    /**
     * 根据reqeuest请求对象，创建{@link RequestContext}实例。绑定系统参数，请求对象
     * @param ropContext
     * @param request
     * @return
     */
    RequestContext buildBySysParams(RopContext ropContext, Object request);

    /**
     * 绑定业务参数
     * @param requestContext
     * @param request
     * @param conversionService
     */
    void bindBusinessParams(RequestContext requestContext);
}

