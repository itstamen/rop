/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-25
 */
package com.rop.sample;

import com.rop.RequestContext;
import com.rop.SecurityManager;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class SampleSecurityManager implements SecurityManager {

    private static final Map<String, Boolean> aclMap = new HashMap<String, Boolean>();

    static {
        aclMap.put("mockSessionId1", true);
        aclMap.put("mockSessionId2", false);
    }

    @Override
    public boolean isGranted(RequestContext methodContext) {
        if(methodContext.getSessionId() != null){
            return aclMap.get(methodContext.getSessionId());
        }else{
            return true;
        }

    }
}

