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

/**
 * @author : chenxh
 * @date: 13-8-13
 */
public abstract class AbstractAuthenticationManager implements AuthenticationManager {

    private String[] appKeys = null;

    private boolean _default = false;


    public String[] appkeys() {
        return appKeys;
    }


    public boolean isDefault() {
        return _default;
    }

    public void setAppKeys(String[] appKeys) {
        this.appKeys = appKeys;
    }

    public void setDefault(boolean _default) {
        this._default = _default;
    }
}
