package com.lo.tinymvc.bean;

import com.lo.tinymvc.bean.base.AnnotatedBeanDefinition;
import com.lo.tinymvc.bean.base.AnnotationMetaData;


/**
 * Created by Administrator on 2017/2/7.
 */
public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private AnnotationMetaData annotationMetaData;

    @Override
    public AnnotationMetaData getAnnotationMetaData() {
        return this.annotationMetaData;
    }

    @Override
    public void setAnnotationMetaData(AnnotationMetaData annotationMetaData) {
        this.annotationMetaData = annotationMetaData;
    }
}
