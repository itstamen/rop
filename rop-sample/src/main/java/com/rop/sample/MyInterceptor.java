/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-4-26
 */
package com.rop.sample;

import com.rop.AbstractInterceptor;
import com.rop.RopResponse;
import com.rop.RopServiceContext;
import com.rop.response.ServiceErrorResponse;
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
public class MyInterceptor extends AbstractInterceptor {

    @Override
    public void beforeService(RopServiceContext context) {
        System.out.println("beforeService ...");

        if("jhonson".equals(context.getRopRequest().getParamValue("userName"))){
            InterceptorResponse response = new InterceptorResponse();
            response.setTestField("the userName can't be jhonson!");
            context.setRopResponse(response);
        }
    }

    @Override
    public void beforeResponse(RopServiceContext context) {
        System.out.println("beforeResponse ...");
    }
}

