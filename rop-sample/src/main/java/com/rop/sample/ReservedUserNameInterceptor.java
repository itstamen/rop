/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-4-26
 */
package com.rop.sample;

import com.rop.AbstractInterceptor;
import com.rop.ServiceMethodContext;
import com.rop.sample.response.InterceptorResponse;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */

@Component
public class ReservedUserNameInterceptor extends AbstractInterceptor {

    @Override
    public void beforeService(ServiceMethodContext methodContext) {
        System.out.println("beforeService ...");

        if("jhonson".equals(methodContext.getRopRequest().getParamValue("userName"))){
            InterceptorResponse response = new InterceptorResponse();
            response.setTestField("the userName can't be jhonson!");
            methodContext.setRopResponse(response);
        }
    }

    @Override
    public void beforeResponse(ServiceMethodContext methodContext) {
        System.out.println("beforeResponse ...");
    }
}

