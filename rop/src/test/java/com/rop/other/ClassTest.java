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
package com.rop.other;

import com.rop.AbstractRopRequest;
import com.rop.RopRequest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ClassTest {

    @Test
    public void testAssignableFrom() {
        assertTrue(!MyRopRequest.class.isAssignableFrom(RopRequest.class));
        assertTrue(!MyRopRequest.class.isAssignableFrom(AbstractRopRequest.class));
        assertTrue(AbstractRopRequest.class.isAssignableFrom(MyRopRequest.class));
    }

    @Test
    public void modeInt() {
        int len = 16 - 1;
        for (int i = 0; i < 100; i++) {
            int i1 = i & len;
            System.out.println("i:" + i1);
            assertTrue(i1 <= len);
        }
    }

    private class MyRopRequest extends AbstractRopRequest {

    }

}

