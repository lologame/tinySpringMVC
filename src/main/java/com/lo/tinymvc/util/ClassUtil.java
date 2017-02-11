package com.lo.tinymvc.util;

import com.lo.tinymvc.bean.BeanReference;
import com.lo.tinymvc.bean.PropertyValue;
import com.lo.tinymvc.bean.PropertyValues;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Administrator on 2017/2/7.
 */
public abstract class ClassUtil {

    public static final char PACKAGE_SEPARATOR = '.';

    public static Set<String> getClassName(String packageName, ClassLoader loader,boolean isRecursion) {
        Set<String> classNames = null;
        String packagePath = packageName.replace(".", "/");

        URL url = loader.getResource(packagePath);
        if (url != null) {
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            } else if (protocol.equals("jar")) {
                JarFile jarFile = null;
                try{
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch(Exception e){
                    e.printStackTrace();
                }

                if(jarFile != null){
                    getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
                }
            }
        } else {
			/*从所有的jar包中查找包名*/
            classNames = getClassNameFromJars(((URLClassLoader)loader).getURLs(), packageName, isRecursion);
        }

        return classNames;
    }


    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Set<String> className = new HashSet<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File childFile : files) {
                if (childFile.isDirectory()) {
                    if (isRecursion) {
                        className.addAll(getClassNameFromDir(childFile.getPath(), packageName+"."+childFile.getName(), isRecursion));
                    }
                } else {
                    String fileName = childFile.getName();
                    if (fileName.endsWith(".class") && !fileName.contains("$")) {
                        className.add(packageName+ "." + fileName.replace(".class", ""));
                    }
                }
            }
        }

        return className;
    }



    private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion){
        Set<String> classNames = new HashSet<String>();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if(!jarEntry.isDirectory()){

                String entryName = jarEntry.getName().replace("/", ".");
                if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {
                    entryName = entryName.replace(".class", "");
                    if(isRecursion){
                        classNames.add(entryName);
                    } else if(!entryName.replace(packageName+".", "").contains(".")){
                        classNames.add(entryName);
                    }
                }
            }
        }

        return classNames;
    }


    private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) {
        Set<String> classNames = new HashSet<String>();

        for (URL url : urls) {
            String classPath = url.getPath();

            //不必搜索classes文件夹
            if (classPath.endsWith("classes/")) {
                continue;
            }

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(classPath.substring(classPath.indexOf("/")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jarFile != null) {
                classNames.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));
            }
        }

        return classNames;
    }

    public static Class<?> getClassByName(String className){
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        Class<?> c= null;
        try {
            c = cl != null ? cl.loadClass(className) : null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static <A extends Annotation> A findAnnotationOnMethod(Method method, Class<A> annotationType){
        return method.getAnnotation(annotationType);
    }

    public static <A extends Annotation> A findAnnotationOnClass(Class classType, Class<A> annotationType){
        return (A) classType.getAnnotation(annotationType);
    }

    public static void applyPropertyValues(PropertyValues pvs , Object obj){

        for (PropertyValue propertyValue : pvs.getPropertyValues()) {
            Object value = propertyValue.getValue();
            try {
                Method declaredMethod = obj.getClass().getDeclaredMethod(
                        "set" + propertyValue.getName().substring(0, 1).toUpperCase()
                                + propertyValue.getName().substring(1), value.getClass());
                declaredMethod.setAccessible(true);

                declaredMethod.invoke(obj, value);
            } catch (NoSuchMethodException e) {
                Field declaredField = null;
                try {
                    declaredField = obj.getClass().getDeclaredField(propertyValue.getName());
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                }
                declaredField.setAccessible(true);
                try {
                    declaredField.set(obj, value);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getShortClassName(Class c){
        String className = c.getName();
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.length();
        String shortName = className.substring(lastDotIndex+1,nameEndIndex);

        return (new StringBuilder()).append(Character.toLowerCase(shortName.charAt(0))).append(shortName.substring(1)).toString();
    }

    public static String getShortClassName(String className){
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.length();
        String shortName = className.substring(lastDotIndex+1,nameEndIndex);

        return (new StringBuilder()).append(Character.toLowerCase(shortName.charAt(0))).append(shortName.substring(1)).toString();
    }
}


