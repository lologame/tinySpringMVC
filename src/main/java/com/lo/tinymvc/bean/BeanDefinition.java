package com.lo.tinymvc.bean;

/**
 * Created by Administrator on 2017/2/6.
 */
public interface BeanDefinition {
     void setBeanClassName(String beanClassName);
     String getBeanClassName();
     void setBeanClass(Class<?> beanClass);
     Class<?> getBeanClass();
     void setPropertyValues(PropertyValues propertyValues);
     PropertyValues getPropertyValues();
}
