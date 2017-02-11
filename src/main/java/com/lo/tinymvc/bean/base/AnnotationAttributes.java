package com.lo.tinymvc.bean.base;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/7.
 */
public class AnnotationAttributes {

    private final Class<? extends Annotation> annotationType;

    private Map<String,Object> annotationAttributesMap = new HashMap<String,Object>(4);


    public AnnotationAttributes() {
        annotationType = null;
    }

    public AnnotationAttributes(Class<? extends Annotation> annotationType){
        this.annotationType = annotationType;
    }

    public void put(String k, Object v){
        annotationAttributesMap.put(k,v);
    }

    public Object getAnnotationAttribute(String k){return this.annotationAttributesMap.get(k);}
}
