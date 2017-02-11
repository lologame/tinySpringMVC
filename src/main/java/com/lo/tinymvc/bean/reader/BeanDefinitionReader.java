package com.lo.tinymvc.bean.reader;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/6.
 */
public interface BeanDefinitionReader {
    void loadBeanDefinitions(String... locations) throws Exception;
    void loadBeanDefinitions(String locations) throws Exception;
}
