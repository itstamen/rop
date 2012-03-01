/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-2-29
 */
package com.stamen.sample.rop;

import com.stamen.rop.ApiMethod;
import com.stamen.sample.rop.request.SampleRopRequest1;
import com.stamen.sample.rop.response.SampleRopResponse1;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@Service
public class SampleRestService {

    @ApiMethod("rop.sample.method1")
    public SampleRopResponse1 method1(SampleRopRequest1 request1) {
        SampleRopResponse1 response = new SampleRopResponse1();
        response.setCreateTime("20120101010101");
        response.setUserId("1");
        return response;
    }
}

