package com.aopbuddy.agent;


import com.aopbuddy.aspect.AspectJPointcut;
import com.aopbuddy.retransform.Context;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


public class Installer {


    public static void agentmain(String agent, Instrumentation instrumentation) {
        System.out.println("Installer agentmain 2param :" + agent);
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Set<String> classPaths = new HashSet<>();
        for (Class<?> loadedClass : allLoadedClasses) {
            ClassLoader classLoader = loadedClass.getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader) classLoader).getURLs();
                for (URL url : urls) {
                    classPaths.add(url.getPath());
                }
            }
        }
        Context.CLASS_PATHS.addAll(classPaths);
        try {
            Context.init(instrumentation);
//            MethodPointcut pointcut = MethodPointcut.of(
//                    "org.example.controller.BeanController", "test", "(..)");
            AspectJPointcut pointcut = new AspectJPointcut("execution(* org.example.*.BeanController.test(..))");
            Context.registerAdvisor(pointcut, new ExampleListener());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
