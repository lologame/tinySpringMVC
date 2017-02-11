package com.lo.tinymvc.bean.base;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */
public class InjectionMetaData {
    private final Class<?> targetClass;

    private final List<Field> injectedElements = new LinkedList<Field>();

    public InjectionMetaData(Class<?> targetClass){
        this.targetClass = targetClass;
    }

    public void addInjectedElements(Field field){
        this.injectedElements.add(field);
    }

    public List<Field> getInjectedElements() {
        return injectedElements;
    }

}
