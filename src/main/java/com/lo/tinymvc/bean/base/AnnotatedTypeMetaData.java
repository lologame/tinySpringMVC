package com.lo.tinymvc.bean.base;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/7.
 */
public interface AnnotatedTypeMetaData {
    boolean isAnnotated(String annotationName);

    AnnotationAttributes getAnnotationAttributes(String annotationName);
    Map<String, AnnotationAttributes> getAllAnnotationAttributes();
    Set<String> getAnnotationSet();

    void addAnnotationAttributes(String annotaitonName,AnnotationAttributes annotationAttributes);
    void addAllAnnotationAttributes(Map<String,AnnotationAttributes> attributesMap);
    void addAnnotationSet(Set<String> annotationSet);
}
