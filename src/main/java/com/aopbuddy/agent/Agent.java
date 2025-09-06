package com.aopbuddy.agent;


import com.aopbuddy.loader.AopAgentLoader;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;


public class Agent {
    public static void agentmain(String agent, Instrumentation instrumentation) {
        startEarthBootstrap(agent, instrumentation);
    }


    private static void startEarthBootstrap(String args, Instrumentation instrumentation) {

        File classFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(classFile.getParent(), "/aopbuddy-1.0-jar-with-dependencies.jar");
        if (!file.exists()) {
            throw new IllegalStateException("找不到文件:" + file);
        } else {
            try {
                AopAgentLoader aopAgentLoader = new AopAgentLoader(new URL[]{file.toURI().toURL()});
                Class<?> aClass = aopAgentLoader.loadClass("com.aopbuddy.agent.BootStrap", true);
                Method premain = aClass.getDeclaredMethod("start", String.class, Instrumentation.class);
                premain.invoke(aClass, args, instrumentation);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException |
                     MalformedURLException var6) {
                Exception e = var6;
                throw new IllegalArgumentException(e);
            }
        }
    }

}
