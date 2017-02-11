package com.lo.tinymvc.bean;

/**
 * Created by Administrator on 2017/2/7.
 */
public interface BeanPostProcessor {
    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
