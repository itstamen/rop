/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-8-3
 */
package com.rop.request;

import org.springframework.core.convert.converter.Converter;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public interface RopConverter<S, T> extends Converter<S, T> {

    /**
     * 从T转换成S
     * @param dest
     * @return
     */
    S unconvert(T dest);
    
    Class<? extends S> getSourceClass();

    Class<? extends T> getTargetClass();
}

