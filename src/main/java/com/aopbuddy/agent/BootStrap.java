package com.aopbuddy.agent;

import cn.hutool.http.HttpUtil;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.servlet.ClassloaderServlet;
import com.aopbuddy.servlet.EvalServlet;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BootStrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class.getName());

    public static void start(String args, Instrumentation instrumentation) {
        try {
            Context.init(instrumentation);
            File classFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            File file = new File(classFile.getParent(), "/agent-jar-with-dependencies.jar");
//            instrumentation.appendToSystemClassLoaderSearch(new JarFile(file));
            MethodPointcut pointcut = MethodPointcut.of(
                    "com.example.demo.controller.DemoController", "test", "(..)");
            MethodListener methodListener = new MethodListener();
            Context.registerAdvisor(pointcut, methodListener);
            HttpUtil.createServer(args != null ? Integer.parseInt(args) : 8888)
                    .addAction("/classloader", new ClassloaderServlet())
                    .addAction("/eval", new EvalServlet())
                    .start();
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE
                    , "BootStrap ERROR ", e);
            throw new RuntimeException(e);
        }
    }
}
