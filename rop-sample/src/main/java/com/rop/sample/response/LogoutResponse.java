/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-7-29
 */
package com.rop.sample.response;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "logonResponse")
public class LogoutResponse{

    @XmlAttribute
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}

