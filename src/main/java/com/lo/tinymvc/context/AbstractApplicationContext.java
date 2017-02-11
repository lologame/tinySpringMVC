package com.lo.tinymvc.context;

import com.lo.tinymvc.bean.BeanDefinition;
import com.lo.tinymvc.bean.BeanPostProcessor;
import com.lo.tinymvc.bean.GenericBeanDefinition;
import com.lo.tinymvc.bean.factory.AbstractBeanFactory;
import com.lo.tinymvc.bean.factory.BeanFactory;
import com.lo.tinymvc.bean.factory.DefaultListableBeanFactory;
import com.lo.tinymvc.bean.processor.AutowireAnnotationBeanPostProcessor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6.
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    private BeanFactory beanFactory;

    public void refresh() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        loadBeanDefinitions(beanFactory);
        registerBeanPostProcessors(beanFactory);
        beanFactory.preInstantiateSingletons();
        this.beanFactory = beanFactory;
    }

    protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;


    private void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
        List<?> beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);
        for (Object beanPostProcessor : beanPostProcessors) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
        }

        BeanPostProcessor beanPostProcessor = new AutowireAnnotationBeanPostProcessor(beanFactory);
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanPostProcessor.getClass());
        beanDefinition.setBeanClassName(beanPostProcessor.getClass().getName());

        beanFactory.registerBeanDefinition(beanPostProcessor.getClass().getName(),beanDefinition);
        beanFactory.addsingletonObject(beanPostProcessor.getClass().getName(),beanPostProcessor);
        beanFactory.addBeanPostProcessor(beanPostProcessor);

    }

    @Override
    public Object getBean(String name) throws Exception{
        return this.beanFactory.getBean(name);
    }

    @Override
    public List<?> getBeansForType(Class type) throws Exception{
        return this.beanFactory.getBeansForType(type);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws Exception {
        return this.beanFactory.findAnnotationOnBean(beanName,annotationType);
    }

    @Override
    public Class<?> getBeanType(String name) throws Exception{
        return this.beanFactory.getBeanType(name);
    }

    @Override
    public List<String> getBeanNames(){
        return this.beanFactory.getBeanNames();
    }
}
