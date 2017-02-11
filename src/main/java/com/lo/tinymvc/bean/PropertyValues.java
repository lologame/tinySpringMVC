package com.lo.tinymvc.bean;

import com.lo.tinymvc.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/7.
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<PropertyValue>();

    public void addPropertyValue(PropertyValue pv) {
        this.propertyValueList.add(pv);
    }

    public List<PropertyValue> getPropertyValues() {
        return this.propertyValueList;
    }

    public PropertyValues(){

    }

    public PropertyValues(Map<String,Object> original){
        if (original != null) {
            for (Map.Entry<?, ?> entry : original.entrySet()) {
                this.propertyValueList.add(new PropertyValue(entry.getKey().toString(), entry.getValue()));
            }
        }
    }

    public PropertyValues(HttpServletRequest request){
        this(WebUtil.getParametersStartingWith(request,null));
    }

}
