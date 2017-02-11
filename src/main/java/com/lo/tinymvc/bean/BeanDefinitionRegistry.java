package com.lo.tinymvc.bean;

/**
 * Created by Administrator on 2017/2/6.
 */
public interface BeanDefinitionRegistry {
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
