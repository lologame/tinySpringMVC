package com.lo.tinymvc.bean.scanner;

import com.lo.tinymvc.annotation.Component;
import com.lo.tinymvc.annotation.Controller;
import com.lo.tinymvc.annotation.RequestMapping;
import com.lo.tinymvc.annotation.Service;
import com.lo.tinymvc.bean.BeanDefinition;
import com.lo.tinymvc.bean.ScannedGenericBeanDefinition;
import com.lo.tinymvc.bean.base.AnnotationAttributes;
import com.lo.tinymvc.bean.base.AnnotationMetaData;
import com.lo.tinymvc.bean.base.MethodMetaData;
import com.lo.tinymvc.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/6.
 */
public class ClassPathBeanDefinitionScanner {

    private static final char PACKAGE_SEPARATOR = '.';

    private static final String ANNOTATION_CONTROLLER = "com.lo.tinymvc.annotation.Controller";
    private static final String ANNOTATION_COMPONENT = "com.lo.tinymvc.annotation.Component";
    private static final String ANNOTATION_SERVICE = "com.lo.tinymvc.annotation.Service";

    private ClassLoader classLoader;

    public ClassPathBeanDefinitionScanner(){
         this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public Set<BeanDefinition> doScan(String... basePackges){
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<BeanDefinition>();
        for(String basePackage : basePackges){
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            beanDefinitions.addAll(candidates);
        }
        return beanDefinitions;
    }

    private Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<BeanDefinition>();
        Set<String> classNames = ClassUtil.getClassName(basePackage,this.classLoader,true);
        for(String className : classNames){
            AnnotationMetaData annotationMetaData = getAnnotationMetaDataByClassName(className);
            if(annotationMetaData == null){
                continue;
            }
            ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setBeanClass(ClassUtil.getClassByName(className));
            beanDefinition.setAnnotationMetaData(annotationMetaData);
            beanDefinitions.add(beanDefinition);
        }
        return beanDefinitions;
    }


    private AnnotationMetaData getAnnotationMetaDataByClassName(String className){
        Class<?> c = ClassUtil.getClassByName(className);
        AnnotationMetaData annotationMetaData = new AnnotationMetaData();
        Annotation[] classAnnotations = c.getAnnotations();
        Set<String> annotationSet = new LinkedHashSet<String>();
        for(Annotation annotation : classAnnotations){
            annotationSet.add(annotation.annotationType().getName());
            AnnotationAttributes annotationAttributes = new AnnotationAttributes(annotation.annotationType());
            refreshAnnotationAttributes(annotationAttributes,annotation);
            annotationMetaData.addAnnotationAttributes(annotation.annotationType().getName(),annotationAttributes);
        }
        annotationMetaData.addAnnotationSet(annotationSet);
        if(!isComponent(annotationMetaData)){
            return null;
        }
        Method[] methods = c.getDeclaredMethods();
        for(Method method:methods){
            MethodMetaData methodMetaData = new MethodMetaData(method.getName(),c.getName());
            Annotation[] methodAnnotations = method.getAnnotations();
            Set<String> methodAnnotationSet = new LinkedHashSet<String>();
            for(Annotation annotation : methodAnnotations){
                methodAnnotationSet.add(annotation.annotationType().getName());
                AnnotationAttributes annotationAttributes = new AnnotationAttributes(annotation.annotationType());
                refreshMethodAnnotationAttributes(annotationAttributes,annotation);
                methodMetaData.addAnnotationAttributes(annotation.annotationType().getName(),annotationAttributes);
            }
            methodMetaData.addAnnotationSet(methodAnnotationSet);
            annotationMetaData.addMethodMetadata(methodMetaData);
        }
        return annotationMetaData;
    }

    private boolean isComponent(AnnotationMetaData annotationMetaData){
        return (annotationMetaData.isAnnotated(ANNOTATION_SERVICE) ||
                annotationMetaData.isAnnotated(ANNOTATION_COMPONENT) || annotationMetaData.isAnnotated(ANNOTATION_CONTROLLER));
    }

   private void refreshAnnotationAttributes(AnnotationAttributes annotationAttributes,Annotation annotation){
        if(annotation instanceof Component){
            annotationAttributes.put("value",((Component)annotation).value());
        }else if(annotation instanceof Controller){
            annotationAttributes.put("value",((Controller)annotation).value());
        }else if(annotation instanceof RequestMapping){
            annotationAttributes.put("value",((RequestMapping)annotation).value());
            annotationAttributes.put("method",((RequestMapping)annotation).method());
        }else if(annotation instanceof Service){
            annotationAttributes.put("value",((Service)annotation).value());
        }
    }

    private void refreshMethodAnnotationAttributes(AnnotationAttributes annotationAttributes,Annotation annotation){
        if(annotation instanceof RequestMapping){
            annotationAttributes.put("value",((RequestMapping)annotation).value());
            annotationAttributes.put("method",((RequestMapping)annotation).method());
        }
    }


    public String generateBeanName(BeanDefinition beanDefinition){
        if(beanDefinition instanceof ScannedGenericBeanDefinition){
            String appointedName = getAppointedBeanName(beanDefinition);
            if(appointedName != null){
                return appointedName;
            }
            else{
                return getDefaultBeanName(beanDefinition);
            }
        }
        else{
            return null;
        }
    }

    private String getDefaultBeanName(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        int lastDotIndex = beanClassName.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = beanClassName.length();
        String shortName = beanClassName.substring(lastDotIndex+1,nameEndIndex);

        return (new StringBuilder()).append(Character.toLowerCase(shortName.charAt(0))).append(shortName.substring(1)).toString();
    }

    private String getAppointedBeanName(BeanDefinition beanDefinition){
        AnnotationMetaData annotationMetaData = ((ScannedGenericBeanDefinition)beanDefinition).getAnnotationMetaData();
        if(annotationMetaData.isAnnotated(ANNOTATION_CONTROLLER)){
            String value = ((String)annotationMetaData.getAnnotationAttributes(ANNOTATION_CONTROLLER).getAnnotationAttribute("value"));
            if(value != null && value.length() >0){
                return value;
            }
        }else if(annotationMetaData.isAnnotated(ANNOTATION_COMPONENT)){
            String value = ((String)annotationMetaData.getAnnotationAttributes(ANNOTATION_COMPONENT).getAnnotationAttribute("value"));
            if(value != null && value.length() >0){
                return value;
            }
        }else if(annotationMetaData.isAnnotated(ANNOTATION_SERVICE)){
            String value = ((String)annotationMetaData.getAnnotationAttributes(ANNOTATION_SERVICE).getAnnotationAttribute("value"));
            if(value != null && value.length() >0){
                return value;
            }
        }else{
            return null;
        }
        return null;
    }
}
