package com.yc.springframework.stereotype;

import java.lang.annotation.*;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-05 14:15
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyBean {
    String value() default "";
}
