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
package com.rop.sample.request;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;
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
@XmlRootElement(name = "address")
public class Address {

    @XmlAttribute
    @Pattern(regexp = "\\w{4,30}")
    private String zoneCode;

    @XmlAttribute
    private String doorCode;

    /**
     * 在请求属性的属性类中，你可以使用接口的集合
     */
    @XmlElementWrapper(name = "streets")
    @XmlElement(name = "street")
    private List<Street> streets;

    @XmlElementWrapper(name = "codes")
    private String[] codes;

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getDoorCode() {
        return doorCode;
    }

    public void setDoorCode(String doorCode) {
        this.doorCode = doorCode;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public void setStreets(List<Street> streets) {
        this.streets = streets;
    }

    public String[] getCodes() {
        return codes;
    }

    public void setCodes(String[] codes) {
        this.codes = codes;
    }
}

