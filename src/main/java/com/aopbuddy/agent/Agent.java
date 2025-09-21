package com.aopbuddy.agent;


import java.io.File;
import java.lang.instrument.Instrumentation;


public class Agent {

    private static volatile String start = null;

    public static void premain(String agent, Instrumentation instrumentation) {
        startEarthBootstrap(agent, instrumentation);
    }

    public static void agentmain(String agent, Instrumentation instrumentation) {
        startEarthBootstrap(agent, instrumentation);
    }


    private static void startEarthBootstrap(String args, Instrumentation instrumentation) {
        if (start != null) {
            return;
        }
        File classFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(classFile.getParent(), "/agent-jar-with-dependencies.jar");
        if (!file.exists()) {
            throw new IllegalStateException("找不到文件:" + file);
        } else {
            BootStrap.start(args, instrumentation);
            start = "started";
//            try {
//                aopAgentLoader = new AopAgentLoader(new URL[]{file.toURI().toURL()});
//                Class<?> aClass = aopAgentLoader.loadClass("com.aopbuddy.agent.BootStrap", true);
//                Method premain = aClass.getDeclaredMethod("start", String.class, Instrumentation.class);
//                premain.invoke(aClass, args, instrumentation);
//            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
//                     IllegalAccessException |
//                     MalformedURLException var6) {
//                Exception e = var6;
//                throw new IllegalArgumentException(e);
//            }
        }
    }

}
