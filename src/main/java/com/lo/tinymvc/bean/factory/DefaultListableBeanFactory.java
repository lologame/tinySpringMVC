package com.lo.tinymvc.bean.factory;

import com.lo.tinymvc.bean.BeanDefinition;
import com.lo.tinymvc.bean.BeanReference;
import com.lo.tinymvc.bean.PropertyValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/2/6.
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory {

    public DefaultListableBeanFactory(){}

    @Override
    protected void applyPropertyValues(Object bean, BeanDefinition mbd) throws Exception {

        for (PropertyValue propertyValue : mbd.getPropertyValues().getPropertyValues()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }
            try {
                Method declaredMethod = bean.getClass().getDeclaredMethod(
                        "set" + propertyValue.getName().substring(0, 1).toUpperCase()
                                + propertyValue.getName().substring(1), value.getClass());
                declaredMethod.setAccessible(true);

                declaredMethod.invoke(bean, value);
            } catch (NoSuchMethodException e) {
                Field declaredField = bean.getClass().getDeclaredField(propertyValue.getName());
                declaredField.setAccessible(true);
                declaredField.set(bean, value);
            }
        }
    }

}
