package com.lo.tinymvc.bean.factory;

import com.lo.tinymvc.bean.BeanDefinition;
import com.lo.tinymvc.bean.BeanDefinitionRegistry;
import com.lo.tinymvc.bean.BeanPostProcessor;

import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/2/6.
 */
public abstract class AbstractBeanFactory implements BeanFactory,BeanDefinitionRegistry {

    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);

    private Map<String , BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private final List<String> beanDefinitionNames = new ArrayList<String>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    @Override
    public List<?> getBeansForType(Class type) throws Exception {
        List beans = new ArrayList<Object>();
        for (String beanDefinitionName : beanDefinitionNames) {
            if (type.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                beans.add(getBean(beanDefinitionName));
            }
        }
        return beans;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition)  {
        beanDefinitionMap.put(name, beanDefinition);
        beanDefinitionNames.add(name);
    }


    @Override
    public Object getBean(String name) throws Exception{
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(name);
        if(beanDefinition == null){
            throw new IllegalArgumentException("No bean named " + name + " is defined");
        }
        Object bean = getSingleton(name);
        if(bean != null){
            return bean;
        }
        return doCreateBean(beanDefinition,name);
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    private Object getSingleton(String beanName){
        return this.singletonObjects.get(beanName);
    }

    public void preInstantiateSingletons() throws Exception {
        for(String name : beanDefinitionNames){
            getBean(name);
        }
    }

    protected Object doCreateBean(BeanDefinition beanDefinition,String beanName) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        populateBean(beanName,beanDefinition,bean);
        addsingletonObject(beanName,bean);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanClass().newInstance();
    }

    protected void populateBean(String beanName,BeanDefinition beanDefinition,Object bean) throws Exception {
        for(BeanPostProcessor beanPostProcessor : beanPostProcessors){
            beanPostProcessor.postProcessAfterInitialization(bean,beanName);
        }
        applyPropertyValues(bean,beanDefinition);
    }

    protected void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {

    }

    public void addsingletonObject(String beanDefinitionName , Object bean){
        this.singletonObjects.put(beanDefinitionName,bean);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws Exception {
        return this.getBean(beanName).getClass().getAnnotation(annotationType);
    }

    @Override
    public Class<?> getBeanType(String beanName) throws Exception{
        return this.beanDefinitionMap.get(beanName).getBeanClass();
    }

    @Override
    public List<String> getBeanNames(){
        return this.beanDefinitionNames;
    }
}
