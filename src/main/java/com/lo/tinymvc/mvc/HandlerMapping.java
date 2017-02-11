package com.lo.tinymvc.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/2/7.
 */
public interface HandlerMapping {
    Object getHandler(HttpServletRequest request) throws Exception;

    void registerHandler(String url,Object handler);
}
