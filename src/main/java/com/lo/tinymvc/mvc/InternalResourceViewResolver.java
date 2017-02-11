package com.lo.tinymvc.mvc;

/**
 * Created by Administrator on 2017/2/9.
 */
public class InternalResourceViewResolver implements ViewResolver {

    private String suffix;
    private String prefix;

    @Override
    public String resolverViewPath(String view) {
        return this.prefix + view + this.suffix;
    }


    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
