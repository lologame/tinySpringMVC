package com.lo.tinymvc.bean.reader;

import com.lo.tinymvc.bean.BeanDefinitionRegistry;
import com.lo.tinymvc.io.ResourceLoader;

/**
 * Created by Administrator on 2017/2/6.
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private ResourceLoader resourceLoader;
    private BeanDefinitionRegistry registry;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {
        this.resourceLoader = resourceLoader;
        this.registry = registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }


}
