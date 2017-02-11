package com.lo.tinymvc.io;

import java.net.URL;

/**
 * Created by Administrator on 2017/2/6.
 */
public class ResourceLoader {

    protected static final String CLASS_PATH_PREFIX = "classpath:";

    public Resource getResource(String location){
        String path = location.substring(CLASS_PATH_PREFIX.length());
        URL resource = this.getClass().getClassLoader().getResource(path);
        return new UrlResouce(resource);
    }
}
