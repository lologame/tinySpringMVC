package com.lo.tinymvc.bean.base;

import com.lo.tinymvc.bean.BeanDefinition;
import com.lo.tinymvc.bean.base.AnnotationMetaData;

/**
 * Created by Administrator on 2017/2/7.
 */
public interface AnnotatedBeanDefinition extends BeanDefinition {
    AnnotationMetaData getAnnotationMetaData();
    void setAnnotationMetaData(AnnotationMetaData annotationMetaData);
}
