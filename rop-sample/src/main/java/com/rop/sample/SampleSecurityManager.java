/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-25
 */
package com.rop.sample;

import com.rop.AbstractSecurityManager;

import java.util.ArrayList;
import java.util.HashMap;
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
public class SampleSecurityManager extends AbstractSecurityManager {

    private static final Map<String, List<String>> aclMap = new HashMap<String, List<String>>();

    static {
        ArrayList<String> serviceMethods = new ArrayList<String>();
        serviceMethods.add("user.logon");
        serviceMethods.add("user.logout");
        serviceMethods.add("user.getSession");
        aclMap.put("00002", serviceMethods);
    }

    @Override
    public boolean isIsvGranted(String appKey,String method,String version) {
        if(aclMap.containsKey(appKey)){
            List<String> serviceMethods = aclMap.get(appKey);
            return serviceMethods.contains(method);
        }else{
            return true;
        }
    }
}

