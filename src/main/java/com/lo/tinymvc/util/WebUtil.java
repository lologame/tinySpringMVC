package com.lo.tinymvc.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/2/9.
 */
public abstract class WebUtil {

    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Enumeration<String> paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                }
                else if (values.length > 1) {
                    params.put(unprefixed, values);
                }
                else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }

    public static void addAttributesOnRequest(HttpServletRequest request,Map<String,Object> attributeMap){
        for(Map.Entry<String ,Object> entry : attributeMap.entrySet()){
            request.setAttribute(entry.getKey(),entry.getValue());
        }
    }
}

