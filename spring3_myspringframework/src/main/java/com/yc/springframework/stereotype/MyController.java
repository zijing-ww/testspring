package com.yc.springframework.stereotype;

import java.lang.annotation.*;

/**
 * @program: testspring
 * @description:
 * @author: zz
 * @create: 2021-04-05 14:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MyComponent
public @interface MyController {
}
