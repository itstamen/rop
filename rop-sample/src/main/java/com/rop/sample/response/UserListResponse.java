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
package com.rop.sample.response;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "userList")
public class UserListResponse {

    @XmlAttribute
    private List<String> userIds = Arrays.asList("1","2","2");

    @XmlElement
    private List<String> userNames = Arrays.asList("a","b","c");

    public List<String> getUserIds() {
        return userIds;
    }

    public List<String> getUserNames() {
        return userNames;
    }
}
