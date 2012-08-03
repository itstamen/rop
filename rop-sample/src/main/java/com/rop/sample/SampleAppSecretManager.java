package com.rop.sample; /**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-5-25
 */

import com.rop.security.AppSecretManager;

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
public class SampleAppSecretManager implements AppSecretManager {

    private static Map<String, String> appKeySecretMap = new HashMap<String, String>();

    static {
        appKeySecretMap.put("00001", "abcdeabcdeabcdeabcdeabcde");
        appKeySecretMap.put("00002","abcdeabcdeabcdeabcdeaaaaa");
        appKeySecretMap.put("00003","abcdeabcdeabcdeabcdeaaaaa");
    }

    @Override
    public String getSecret(String appKey) {
        System.out.println("use SampleAppSecretManager!");
        return appKeySecretMap.get(appKey);
    }

    @Override
    public boolean isValidAppKey(String appKey) {
        return getSecret(appKey) != null;
    }
}

