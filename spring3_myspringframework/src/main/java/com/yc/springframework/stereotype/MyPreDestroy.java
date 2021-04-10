package com.yc.springframework.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-05 14:18
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MyPreDestroy {
}
