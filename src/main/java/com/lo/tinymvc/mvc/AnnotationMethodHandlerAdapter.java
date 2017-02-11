package com.lo.tinymvc.mvc;

import com.lo.tinymvc.annotation.RequestMapping;
import com.lo.tinymvc.annotation.RequestMappingInfo;
import com.lo.tinymvc.bean.base.MethodResolver;
import com.lo.tinymvc.util.ClassUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/2/8.
 */
public class AnnotationMethodHandlerAdapter implements HandlerAdapter {

    private final Map<Class<?>, MethodResolver> methodResolverCache =
            new ConcurrentHashMap<Class<?>, MethodResolver>(64);

    @Override
    public boolean supports(Object handler) {
        return getMethodResolver(handler).hasHandlerMethods();
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MethodResolver methodResolver = getMethodResolver(handler);
        if(methodResolver == null){
            throw new Exception("No HandlerMethod Found !");
        }
        Method method = methodResolver.resolveMethod(request);
        ModelMap model = new ModelMap();
        return new ModelAndView((String) invokeHandlerMethod(method,handler,methodResolver,request,model),model);
    }

    private Object invokeHandlerMethod(Method handlerMethod , Object handler , MethodResolver methodResolver,
                                       HttpServletRequest request, ModelMap model) throws InvocationTargetException, IllegalAccessException {
        Object[] args = methodResolver.resolveHandlerArguments(handlerMethod,request,model);
        return handlerMethod.invoke(handler,args);
    }


    private MethodResolver getMethodResolver(Object handler){
        MethodResolver methodResolver = this.methodResolverCache.get(handler.getClass());
        if(methodResolver == null){
            Class handlerType = handler.getClass();
            Set<Method> handlerMethods = new LinkedHashSet<Method>();
            Map<Method, RequestMappingInfo> mappings = new HashMap<Method, RequestMappingInfo>();
            RequestMapping typeLevelMapping = ClassUtil.findAnnotationOnClass(handlerType,RequestMapping.class);
            if(typeLevelMapping == null){
                return null;
            }
            for(Method method : handlerType.getDeclaredMethods()){
                RequestMapping requestMapping = ClassUtil.findAnnotationOnMethod(method,RequestMapping.class);
                if(requestMapping != null){
                    handlerMethods.add(method);
                    mappings.put(method,new RequestMappingInfo(requestMapping.value(),requestMapping.method()));
                }
            }
            if(!mappings.isEmpty()){
                methodResolver = new MethodResolver();
                methodResolver.setTypeLevelMapping(typeLevelMapping);
                methodResolver.addHandlerMethods(handlerMethods);
                methodResolver.putMappings(mappings);
                this.methodResolverCache.put(handlerType,methodResolver);
            }
        }
        return methodResolver;
    }
}
