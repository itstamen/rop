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
