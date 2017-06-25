/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

