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
package com.rop.session;

import com.rop.CommonConstant;

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
@SuppressWarnings("serial")
public  abstract class AbstractSession implements Session {

    private Map<String, Object> attributes = new HashMap<String, Object>();


    public void setAttribute(String name, Object obj) {
        markChanged();
        attributes.put(name, obj);
    }


    public Object getAttribute(String name) {
        markChanged();
        return attributes.get(name);
    }


    public Map<String, Object> getAllAttributes() {
        Map<String, Object> tempAttributes = new HashMap<String, Object>(attributes.size());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            if (!CommonConstant.SESSION_CHANGED.equals(entry.getKey())) {
                tempAttributes.put(entry.getKey(),entry.getValue());
            }
        }
        return tempAttributes;
    }


    public void removeAttribute(String name) {
        markChanged();
        attributes.remove(name);
    }


    public boolean isChanged() {
        return attributes.containsKey(CommonConstant.SESSION_CHANGED);
    }

    private void markChanged(){
        attributes.put(CommonConstant.SESSION_CHANGED,Boolean.TRUE);
    }
}

