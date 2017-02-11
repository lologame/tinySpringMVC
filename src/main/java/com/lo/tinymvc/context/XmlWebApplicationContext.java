package com.lo.tinymvc.context;

import com.lo.tinymvc.bean.factory.AbstractBeanFactory;
import com.lo.tinymvc.bean.factory.BeanFactory;
import com.lo.tinymvc.bean.factory.DefaultListableBeanFactory;
import com.lo.tinymvc.bean.reader.XmlBeanDefinitionReader;
import com.lo.tinymvc.io.ResourceLoader;

/**
 * Created by Administrator on 2017/2/6.
 */
public class XmlWebApplicationContext extends AbstractApplicationContext {

    private String[] configLocations;

    public XmlWebApplicationContext(String[] configLocations){
        this.configLocations = configLocations;
    }

    @Override
    protected void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader(),beanFactory);
        loadBeanDefinitions(beanDefinitionReader);
    }

    private void loadBeanDefinitions(XmlBeanDefinitionReader beanDefinitionReader) throws Exception {
        if(this.configLocations != null){
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }
}
