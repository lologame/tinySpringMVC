package com.lo.tinymvc.bean;

/**
 * Created by Administrator on 2017/2/7.
 */
public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String beanClassName;
    private PropertyValues propertyValues = new PropertyValues();

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    @Override
    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }
}
