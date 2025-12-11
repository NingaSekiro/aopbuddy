package com.aopbuddy.vmtool;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.reflection.SunClassLoader;
import org.codehaus.groovy.runtime.callsite.CallSiteClassLoader;

import java.util.HashSet;
import java.util.Set;

import static com.aopbuddy.retransform.Context.inst;

public class ClassUtil {

    public static Set<ClassLoader> getClassLoaders() {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        Set<ClassLoader> classLoaders = new HashSet<>();
        for (Class<?> loadedClass : allLoadedClasses) {
            ClassLoader classLoader = loadedClass.getClassLoader();
            if (classLoader != null && !(classLoader instanceof GroovyClassLoader.InnerLoader) && !(classLoader instanceof CallSiteClassLoader) && !(classLoader instanceof SunClassLoader) && !classLoader.getClass().getSimpleName().equals("DelegatingClassLoader") && !classLoader.getClass().getSimpleName().equals("NoCallStackClassLoader")) {
                classLoaders.add(classLoader);
            }
        }
        return classLoaders;
    }


    public static ClassLoader getDefaultClassLoader() {
        for (ClassLoader classLoader : getClassLoaders()) {
            if (classLoader.getClass().getName().contains("ParallelWebappClassLoader")) {
                return classLoader;
            }
        }
        return ClassLoader.getSystemClassLoader();
    }
}
