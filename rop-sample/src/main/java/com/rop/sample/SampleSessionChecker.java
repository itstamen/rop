/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-25
 */
package com.rop.sample;

import com.rop.validation.SessionChecker;

import java.util.ArrayList;
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
public class SampleSessionChecker implements SessionChecker{
    
    private static final List<String> invalidSessionIds = new ArrayList<String>();
    static {
        invalidSessionIds.add("mockSessionId1");
        invalidSessionIds.add("mockSessionId2");
    }
    
    @Override
    public boolean isValid(String sessionId) {
        return invalidSessionIds.contains(sessionId);
    }
}

