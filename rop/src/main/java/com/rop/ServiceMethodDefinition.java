/**
 * 版权声明： 版权所有 违者必究 2012
 * 日    期：12-5-31
 */
package com.rop;


import com.rop.annotation.HttpAction;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ServiceMethodDefinition {

    /**
     * 默认的组
     */
    public static final String DEFAULT_GROUP = "DEFAULT";

    /**
     * 默认分组标识
     */
    public static final String DEFAULT_GROUP_TITLE = "DEFAULT GROUP";

    /**
     * API的方法
     */
    private String method;

    /**
     * API的方法的标识
     */
    private String methodTitle;

    /**
     * HTTP请求的方法
     */
    private HttpAction[] httpAction;

    /**
     * API方法所属组名
     */
    private String methodGroup = DEFAULT_GROUP;

    /**
     * API方法组名的标识
     */
    private String methodGroupTitle;

    /**
     * API所属的标签
     */
    private String[] tags = {};

    /**
     * 过期时间，单位为秒，0或负数表示不过期
     */
    private int timeout = -9999;

    /**
     * 对应的版本号，如果为null或""表示不区分版本
     */
    private String version = null;

    /**
     * 是否需要进行会话校验
     */
    private boolean needInSession;

    /**
     * 是否忽略服务请求签名的校验，默认为false
     */
    private boolean ignoreSign = false;

    /**
     * 服务方法是否已经过期
     */
    private boolean obsoleted = false;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getMethodGroup() {
        return methodGroup;
    }

    public void setMethodGroup(String methodGroup) {
        this.methodGroup = methodGroup;
    }

    public String getMethodGroupTitle() {
        return methodGroupTitle;
    }

    public void setMethodGroupTitle(String methodGroupTitle) {
        this.methodGroupTitle = methodGroupTitle;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isNeedInSession() {
        return needInSession;
    }

    public void setNeedInSession(boolean needInSession) {
        this.needInSession = needInSession;
    }

    public boolean isIgnoreSign() {
        return ignoreSign;
    }

    public void setIgnoreSign(boolean ignoreSign) {
        this.ignoreSign = ignoreSign;
    }

    public HttpAction[] getHttpAction() {
        return httpAction;
    }

    public void setHttpAction(HttpAction[] httpAction) {
        this.httpAction = httpAction;
    }

    public boolean isObsoleted() {
        return obsoleted;
    }

    public void setObsoleted(boolean obsoleted) {
        this.obsoleted = obsoleted;
    }
}

