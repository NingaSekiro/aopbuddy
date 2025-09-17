package com.aopbuddy.agent;

import cn.hutool.http.HttpUtil;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.servlet.ClassloaderServlet;
import com.aopbuddy.servlet.EvalServlet;

import java.lang.instrument.Instrumentation;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BootStrap {
    private static final Logger LOGGER = Logger.getLogger(BootStrap.class.getName());

    public static void start(String args, Instrumentation instrumentation) {
        try {
            Context.init(instrumentation);
            MethodPointcut pointcut = MethodPointcut.of(
                    "com.myth.earth.rmi.server.ConsoleRemoteServer", "*", "(..)");
            ExampleListener exampleListener = new ExampleListener();
//            Context.registerAdvisor(pointcut, exampleListener);
            System.out.println("agentmain" + pointcut);
            System.out.println("agentmain" + exampleListener);
            HttpUtil.createServer(args != null ? Integer.parseInt(args) : 8888)
                    .addAction("/classloader", new ClassloaderServlet())
                    .addAction("/eval", new EvalServlet())
                    .start();
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING
                    , "BootStrap ERROR ", e);
            throw new RuntimeException(e);
        }

    }
}
