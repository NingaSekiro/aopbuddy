package com.aopbuddy.agent;


import com.aopbuddy.aspect.AspectJPointcut;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Context;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

import static com.aopbuddy.retransform.Context.CLASS_LOADER;


public class Installer {

//    TomcatEmbeddedWebappClassLoader
    public static void agentmain(String agent, Instrumentation instrumentation) {
        System.out.println("Installer agentmain 2param :" + agent);
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        Set<ClassLoader> classLoaders = new HashSet<>();
        for (Class<?> loadedClass : allLoadedClasses) {
            ClassLoader classLoader = loadedClass.getClassLoader();
            classLoaders.add(classLoader);
            if (classLoader != null && classLoader.toString().contains("ParallelWebappClassLoader")) {
                CLASS_LOADER = classLoader;
            }
        }
        System.out.println("Installer agentmain 3param :" + agent);
        try {
            Context.init(instrumentation);
            System.out.println("555555555555555555555 :" + agent);
//            MethodPointcut pointcut = MethodPointcut.of(
//                    "org.example.controller.BeanController", "test", "(..)");
//            MethodPointcut pointcut = MethodPointcut.of(
//                    "org.example.controller.BeanController", "test", "(..)");

//            MethodPointcut pointcut = MethodPointcut.of(
//                    "demo.TargetClass", "sayHello", "(..)");
            AspectJPointcut pointcut = new AspectJPointcut("execution(* org.example.*.BeanController.test(..))");
            MethodPointcut pointcut2 = MethodPointcut.of(
                    "org.example.controller.BeanController", "beanList", "(..)");
            ExampleListener exampleListener = new ExampleListener();
//            Example2Listener exampleListener = new Example2Listener();
            Context.registerAdvisor(pointcut, exampleListener);
            System.out.println("agentmain" + pointcut);
            System.out.println("agentmain" + exampleListener);
//            Context.registerAdvisor(pointcut2, new Example1Listener());
//            Context.registerAdvisor(pointcut2, new Example1Listener());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
