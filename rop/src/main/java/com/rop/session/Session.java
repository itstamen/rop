package com.rop.session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author chenxh
 */
public interface Session extends Serializable {

    /**
     * 设置属性
     * @param name
     * @param obj
     */
    void setAttribute(String name, Object obj);

    /**
     * 获取属性
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * 获取所有的属性
     * @return
     */
    Map<String, Object> getAllAttributes();

    /**
     * 删除属性条目
     * @param name
     */
    void removeAttribute(String name);

    /**
     * ROP使用外部缓存管理器保存{@link Session}的内容，在每次接收到请求时，从外部缓存服务器复制会话并反序列化出
     * 会话对象{@link Session}。在处理请求时，业务逻辑允许更改{@link Session}中的内容，所以需要在{@link Session}
     * 内容发生变化后，将 {@link Session}重写到外部缓存中。
     *
     * 如果每次访问会话后，ROP都重写会话，将比较影响性能，往往大部分的请求并不会更改会话的内容。
     * 所以需要有一种机制在{@link Session}内容发生变更后告之ROP，以便ROP在{@link Session}内容发生变化时，
     * 在请求结束时，将其同步到外部缓存服务器中。
     *
     * ROP根据{@code isChanged}判断会话内容是否有变化，监测到会话内容的变化，它将自动执行会话重写操作，以便将本
     * 些更改同步到外部缓存服务器中。
     */
    boolean isChanged();

}