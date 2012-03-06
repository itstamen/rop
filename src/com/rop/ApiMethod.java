/**
 *
 * 日    期：12-2-10
 */
package com.rop;


import java.lang.annotation.*;

/**
 * <pre>
 *     使用该注解对服务方法进行标注，这些方法必须是Spring的Service:既打了@Service的注解。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {
    String value();

    boolean needInSession() default true;
}
