/**
 *
 * 日    期：12-2-13
 */
package com.rop;

import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *      BOP的服务路由器，服务方法必须位于@Controller的类中，服务方法使用{@link ApiMethod}注解，有两个合法的方法签名方式：
 * 签名方式1：有入参，且参数必须实现{@link RopRequest}接口，返回参数为BopResponse
 *   <code>
 *    @BopServiceMethod
 *    BopResponse handleMethod1(Handle1BopRequest handle1BopRequest){
 *        ...
 *    }
 *   </code>
 * 签名方式2：无入参，返回参数为BopResponse
 *   <code>
 *    @BopServiceMethod
 *    BopResponse handleMethod1(){
 *        ...
 *    }
 *   </code>
 *   BOP框架会自动将请求参数的值绑定到入参请求对象中。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopServiceRouter {

    /**
     * ROP框架的总入口，一般框架实现，开发者无需关注。
     * @param webRequest
     * @param httpServletResponse
     */
    void service(HttpServletRequest request, HttpServletResponse response);
}

