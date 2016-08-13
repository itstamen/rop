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

import org.testng.annotations.Test;

import java.text.MessageFormat;

/**
 * @author : chenxh(quickselect@163.com)
 * @date: 2014/7/4
 */
public class MessageFormatTest {

    @Test
    public void testFormat(){
        String str = MessageFormat.format("aaa{0}bbb{1}", "X");
        System.out.println(str);
        str = MessageFormat.format("aaa bbb", "X","Y");
        System.out.println(str);
}
}
