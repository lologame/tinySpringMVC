package com.lo.tinymvc.bean.processor;

import com.lo.tinymvc.annotation.Autowired;
import com.lo.tinymvc.bean.BeanPostProcessor;
import com.lo.tinymvc.bean.base.InjectionMetaData;
import com.lo.tinymvc.bean.factory.BeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7.
 */
public class AutowireAnnotationBeanPostProcessor implements BeanPostProcessor {

    private BeanFactory beanFactory;

    public AutowireAnnotationBeanPostProcessor(BeanFactory beanFactory){
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        InjectionMetaData injectionMetaData = findAutowiringMetadata(bean);
        if (injectionMetaData != null) {
            inject(injectionMetaData,bean);
        }
        return bean;
    }

    private InjectionMetaData findAutowiringMetadata(Object bean){
        Class<?> beanClass = bean.getClass();
        InjectionMetaData injectionMetaData = new InjectionMetaData(beanClass);
        Field[] fields = beanClass.getDeclaredFields();
        boolean isFound = false;
        for(Field field : fields){
            Annotation[] annotations = field.getAnnotations();
            for(Annotation annotation : annotations){
                if(annotation instanceof Autowired){
                    injectionMetaData.addInjectedElements(field);
                    isFound = true;
                    break;
                }
            }
        }
        if(!isFound){
            return null;
        }else {
            return injectionMetaData;
        }
    }

    private void inject(InjectionMetaData injectionMetaData,Object object) throws Exception {
        List<Field> injectedElements = injectionMetaData.getInjectedElements();
        for(Field field : injectedElements){
            List<?> matchedObject = this.beanFactory.getBeansForType(field.getType());
            Object value = null;
            if(matchedObject != null && (!matchedObject.isEmpty())){
                value = matchedObject.get(0);
            }
            field.setAccessible(true);
            field.set(object,value);
        }
    }
}
