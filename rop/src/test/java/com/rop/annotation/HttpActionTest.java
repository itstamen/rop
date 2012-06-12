/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-8
 */
package com.rop.annotation;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class HttpActionTest {

    @Test
    public void testValueOf() {
        assertEquals(HttpAction.valueOf("GET"), HttpAction.GET);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void invalidValueOf() {
        assertEquals(HttpAction.valueOf("get"), HttpAction.GET);
    }
}

