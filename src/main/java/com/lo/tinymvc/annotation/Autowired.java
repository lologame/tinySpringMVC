package com.lo.tinymvc.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/2/7.
 */
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
