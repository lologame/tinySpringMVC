package com.lo.tinymvc.mvc;

import com.lo.tinymvc.util.PathUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/7.
 */
public class DefaultAnnotationHandlerMapping implements HandlerMapping {

    private final Map<String, Object> handlerMap = new LinkedHashMap<String, Object>();

    @Override
    public Object getHandler(HttpServletRequest request) throws Exception {
        String urlPath = PathUtil.getPathWithinApplication(request);
        Object handler = this.handlerMap.get(urlPath);
        if(handler != null){
            return handler;
        }
        for(String registeredPattern : this.handlerMap.keySet()){
            if(PathUtil.pathMatch(registeredPattern,urlPath)){
                return this.handlerMap.get(registeredPattern);
            }
        }
        return null;
    }

    @Override
    public void registerHandler(String url, Object handler) {
        this.handlerMap.put(url,handler);
    }


}
