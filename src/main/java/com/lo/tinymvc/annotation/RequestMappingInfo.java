package com.lo.tinymvc.annotation;

import com.lo.tinymvc.annotation.RequestMethod;

/**
 * Created by Administrator on 2017/2/8.
 */
public class RequestMappingInfo {

    private final String[] patterns;
    private final RequestMethod[] methods;


    public RequestMappingInfo(String[] patterns, RequestMethod[] methods) {
        this.patterns = patterns;
        this.methods = methods;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public RequestMethod[] getMethods() {
        return methods;
    }
}
