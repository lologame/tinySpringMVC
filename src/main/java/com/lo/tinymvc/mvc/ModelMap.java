package com.lo.tinymvc.mvc;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/2/8.
 */
public class ModelMap extends LinkedHashMap<String,Object>{

    public ModelMap(){}

    public ModelMap(String attributeName, Object attributeValue) {
        addAttribute(attributeName, attributeValue);
    }

    public ModelMap addAttribute(String attributeName, Object attributeValue) {
        put(attributeName, attributeValue);
        return this;
    }

    public boolean containsAttribute(String attributeName) {
        return containsKey(attributeName);
    }

}
