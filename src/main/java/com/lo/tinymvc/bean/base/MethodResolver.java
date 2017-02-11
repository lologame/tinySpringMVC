package com.lo.tinymvc.bean.base;

import com.lo.tinymvc.annotation.RequestMapping;
import com.lo.tinymvc.annotation.RequestMappingInfo;
import com.lo.tinymvc.annotation.RequestMethod;
import com.lo.tinymvc.bean.PropertyValues;
import com.lo.tinymvc.mvc.ModelMap;
import com.lo.tinymvc.util.ClassUtil;
import com.lo.tinymvc.util.PathUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/8.
 */
public class MethodResolver {

    private final Map<Method, RequestMappingInfo> mappings = new HashMap<Method, RequestMappingInfo>();

    private final Set<Method> handlerMethods = new LinkedHashSet<Method>();

    private RequestMapping typeLevelMapping;

    public final Set<Method> getHandlerMethods() {
        return this.handlerMethods;
    }

    public final boolean hasHandlerMethods() {
        return !this.handlerMethods.isEmpty();
    }

    public void addHandlerMethods(Set<Method> handlerMethods){
        this.handlerMethods.addAll(handlerMethods);
    }

    public void putMappings(Map<Method,RequestMappingInfo> mappings){
        this.mappings.putAll(mappings);
    }

    public RequestMapping getTypeLevelMapping() {
        return typeLevelMapping;
    }

    public void setTypeLevelMapping(RequestMapping typeLevelMapping) {
        this.typeLevelMapping = typeLevelMapping;
    }

    public Method resolveMethod(HttpServletRequest request) throws Exception {
        String lookupPath = PathUtil.getPathWithinApplication(request);
        for(Method handlerMethod : getHandlerMethods()){
            Set<String> pathPatterns = PathUtil.generateCombinedPathPatterns(getTypeLevelMapping().value(),
                    this.mappings.get(handlerMethod).getPatterns());
            for(String pathPattern : pathPatterns){
                if(PathUtil.pathMatch(pathPattern,lookupPath) &&
                        requestMethodMatch(request,this.mappings.get(handlerMethod).getMethods())){
                    return handlerMethod;
                }
            }
        }
        throw new Exception("No HandlerMethod Found！");
    }

    private boolean requestMethodMatch(HttpServletRequest request, RequestMethod[] requestMethods){
        if(requestMethods.length == 0){
            return true;
        }
        for(RequestMethod requestMethod : requestMethods){
            if((request.getMethod().equals("POST") && requestMethod.equals(RequestMethod.POST))
                    ||(request.getMethod().equals("GET") && requestMethod.equals(RequestMethod.GET))){
                return true;
            }
        }
        return false;
    }

    public Object[] resolveHandlerArguments(Method handlerMethod, HttpServletRequest request,
                                            ModelMap model){
        Class<?>[] paramTypes = handlerMethod.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        for(int i = 0 ;i < args.length;i++){
            Class<?> paramType = paramTypes[i];
            if(ModelMap.class.isAssignableFrom(paramType)){
                args[i] = model;
            }
            else{
                args[i] = getBindedArgWithRequest(request,paramType);
                model.addAttribute(ClassUtil.getShortClassName(paramType),args[i]);
            }
        }
        return args;
    }

    private Object getBindedArgWithRequest(HttpServletRequest request,Class argType){
        PropertyValues propertyValues = new PropertyValues(request);
        Object arg = null;
        try {
            arg = argType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        ClassUtil.applyPropertyValues(propertyValues,arg);
        return arg;
    }

}
