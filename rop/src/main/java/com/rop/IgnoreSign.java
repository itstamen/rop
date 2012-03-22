/**
 *
 * 日    期：12-2-10
 */
package com.rop;


import java.lang.annotation.*;

/**
 * <pre>
 *     请求对象（{@link RopRequest}）中不需要签名的属性（默认都要签名）。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreSign {
}
