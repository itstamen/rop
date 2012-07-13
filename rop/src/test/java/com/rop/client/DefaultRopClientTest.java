/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-30
 */
package com.rop.client;

import org.testng.annotations.Test;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class DefaultRopClientTest {

    private RopClient ropClient = new DefaultRopClient("http://localhost:8088/router", "00001", "abcdeabcdeabcdeabcdeabcde");

    @Test
    public void testPostWithSession() throws Exception {

    }
}

