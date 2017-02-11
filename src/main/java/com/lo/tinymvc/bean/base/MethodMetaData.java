package com.lo.tinymvc.bean.base;

import java.util.*;

/**
 * Created by Administrator on 2017/2/7.
 */
public class MethodMetaData implements AnnotatedTypeMetaData{
    private final String methodName;
    private final String declaringClassName;

    private final Set<String> annotationSet = new LinkedHashSet<String>(4);

    private final Map<String,AnnotationAttributes> attributesMap = new HashMap<String, AnnotationAttributes>(4);

    public MethodMetaData(String methodName,String declaringClassName){
        this.methodName = methodName;
        this.declaringClassName = declaringClassName;
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return this.attributesMap.containsKey(annotationName);
    }

    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationName) {
        return attributesMap.get(annotationName);
    }

    @Override
    public Map<String, AnnotationAttributes> getAllAnnotationAttributes() {
        return this.attributesMap;
    }

    @Override
    public Set<String> getAnnotationSet() {
        return annotationSet;
    }

    @Override
    public void addAnnotationAttributes(String annotaitonName,AnnotationAttributes annotationAttributes) {
        this.attributesMap.put(annotaitonName,annotationAttributes);
    }

    @Override
    public void addAllAnnotationAttributes(Map<String, AnnotationAttributes> attributesMap) {
        this.attributesMap.putAll(attributesMap);
    }

    @Override
    public void addAnnotationSet(Set<String> annotationSet) {
        this.annotationSet.addAll(annotationSet);
    }

}
