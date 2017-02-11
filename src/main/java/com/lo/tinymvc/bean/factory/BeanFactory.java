package com.lo.tinymvc.bean.factory;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */
public interface BeanFactory {
    Object getBean(String name) throws Exception;

    List<?> getBeansForType(Class type) throws Exception;

    <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws Exception;

    Class<?> getBeanType(String name) throws Exception;

    List<String> getBeanNames();

}
