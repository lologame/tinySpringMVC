package com.lo.tinymvc.mvc;

import com.lo.tinymvc.annotation.RequestMapping;
import com.lo.tinymvc.context.ApplicationContext;
import com.lo.tinymvc.context.XmlWebApplicationContext;
import com.lo.tinymvc.util.ClassUtil;
import com.lo.tinymvc.util.PathUtil;
import com.lo.tinymvc.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/6.
 */
public class DispatchServlet extends HttpServlet{

    private String[] configLocations;

    private ApplicationContext webApplicationContext;

    private HandlerMapping handlerMapping;

    private List<HandlerAdapter> handlerAdapters;

    private List<ViewResolver> viewResolvers;

    @Override
    public final void init() throws ServletException{
        this.configLocations = this.getServletConfig().getInitParameter("contextConfigLocation").split(",");
        try {
            this.webApplicationContext = initWebApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            initStrategies();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApplicationContext initWebApplicationContext() throws Exception {
        return  createWebApplicationContext(configLocations);
    }

    private ApplicationContext createWebApplicationContext(String[] configLocations) throws Exception {
        XmlWebApplicationContext wac = new XmlWebApplicationContext(configLocations);
        wac.refresh();
        return wac;
    }

    private void initHandlerMapping() throws Exception {
        HandlerMapping handlerMapping = new DefaultAnnotationHandlerMapping();
        for(String beanName : this.webApplicationContext.getBeanNames()){
            String[] urls = determinUrlsForHandler(beanName);
            for(String url : urls){
                handlerMapping.registerHandler(url,this.webApplicationContext.getBean(beanName));
            }
        }
        this.handlerMapping = handlerMapping;
    }

    @SuppressWarnings("unchecked")
    private void initHandlerAdapters() throws Exception {
        this.handlerAdapters = new ArrayList<HandlerAdapter>();
        handlerAdapters.add(new AnnotationMethodHandlerAdapter());
        List internalHandlerAdapters = this.webApplicationContext.getBeansForType(HandlerAdapter.class);
        this.handlerAdapters.addAll(internalHandlerAdapters);
    }

    @SuppressWarnings("unchecked")
    private void initViewResolvers() throws Exception {
        this.viewResolvers = new ArrayList<ViewResolver>();
        List internalViewResolvers = this.webApplicationContext.getBeansForType(ViewResolver.class);
        this.viewResolvers.addAll(internalViewResolvers);
    }

    private String[] determinUrlsForHandler(String beanName) throws Exception {
        Class handlerType = this.webApplicationContext.getBeanType(beanName);
        Set<String> urls = new LinkedHashSet<String>();
        RequestMapping mapping = this.webApplicationContext.findAnnotationOnBean(beanName,RequestMapping.class);
        if(mapping != null) {
            String[] typeLevelPatterns = mapping.value();
            if (typeLevelPatterns.length > 0){
                String[] methodLevlevelPatterns = determineUrlsForHandlerMethods(handlerType);
                for(String typeLevelPattern : typeLevelPatterns){
                    for(String methodLevlePattern : methodLevlevelPatterns){
                        String combinedPattern = typeLevelPattern + methodLevlePattern;
                        urls.add(combinedPattern);
                    }
                }
            }
        }
        return urls.toArray(new String[(urls.size())]);
    }

    private String[] determineUrlsForHandlerMethods(Class<?> handlerType){
        Set<String> urls = new LinkedHashSet<String>();
        Method[] methods = handlerType.getDeclaredMethods();
        for(Method method : methods){
            RequestMapping mapping = ClassUtil.findAnnotationOnMethod(method,RequestMapping.class);
            if(mapping != null){
                String[] mappedPatterns = mapping.value();
                if(mappedPatterns.length>0) {
                    for (String mappedPattern : mappedPatterns) {
                        if(mappedPattern.length()>0) {
                            PathUtil.addUrlsForPath(urls, mappedPattern);
                        }
                    }
                }
            }
        }
        return urls.toArray(new String[urls.size()]);
    }


    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception{

        Object mappedHandler = getHandler(request);

        HandlerAdapter handlerAdapter = getHandlerAdapter(mappedHandler);

        ModelAndView mv = handlerAdapter.handle(request, response, mappedHandler);

        render(mv,request,response);

    }

    private Object getHandler(HttpServletRequest request) throws Exception {
        Object handler = this.handlerMapping.getHandler(request);
        if(handler == null){
            throw new Exception("No Handler Found!");
        }
        return handler;
    }

    private HandlerAdapter getHandlerAdapter(Object handler) throws Exception {
        for(HandlerAdapter handlerAdapter : this.handlerAdapters){
            if(handlerAdapter.supports(handler)){
                return handlerAdapter;
            }
        }
        throw new Exception("No HandlerAdapter Found");
    }

    private void render(ModelAndView mv,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dispathcUrl = resolveViewName(mv.getView());
        WebUtil.addAttributesOnRequest(request,mv.getModel());
        request.getRequestDispatcher(dispathcUrl).forward(request,response);
    }

    private String resolveViewName(String viewName){
        for(ViewResolver viewResolver : this.viewResolvers){
            String resolvedViewName = viewResolver.resolverViewPath(viewName);
            if(resolvedViewName != null){
                return resolvedViewName;
            }
        }
        return null;
    }

    private void initStrategies() throws Exception {
        initHandlerMapping();
        initHandlerAdapters();
        initViewResolvers();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatch(request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
