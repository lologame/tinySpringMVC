package com.lo.tinymvc.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/2/7.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String[] value() default {};
    RequestMethod[] method() default {};
}
