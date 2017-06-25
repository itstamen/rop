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
package com.rop.sample.converter;

import com.rop.converter.RopConverter;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: stamen
 * Date: 13-10-25
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class CollectionsConverter implements RopConverter<String, Collection<?>> {


    public String unconvert(Collection<?> target) {
        return null;
    }


    public Class<String> getSourceClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Class<Collection<?>> getTargetClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Collection<?> convert(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
