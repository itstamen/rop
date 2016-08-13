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


import com.rop.sample.sys.DateXmlAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "createUserResponse")
public class CreateUserResponse{

    @XmlAttribute
    private String userId;

    @XmlAttribute
    private String createTime;

    @XmlElement
    private Foo foo = new Foo("0","0");

    @XmlElement
    private String feedback;

    @XmlAttribute
    private Boolean status = true;

    @XmlAttribute
    private Integer age = 10;

    @XmlAttribute
    private Float height = 170.01f;

    @XmlElement
    private List<Foo> fooList;

    @XmlElement
    private LinkedHashMap<String,String> maps = new LinkedHashMap<String,String>();
    {
        maps.put("a","a");
        maps.put("b","b");
        maps.put("c","c");
    }

    @XmlElement
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Date date;

    private boolean ok = true;

    public List<Foo> getFooList() {
        return fooList;
    }

    public void setFooList(List<Foo> fooList) {
        this.fooList = fooList;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFeedback() {
        return feedback;
    }

    public LinkedHashMap<String, String> getMaps() {
        return maps;
    }

    public void setMaps(LinkedHashMap<String, String> maps) {
        this.maps = maps;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

