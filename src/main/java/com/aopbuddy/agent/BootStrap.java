package com.aopbuddy.agent;

import cn.hutool.http.HttpUtil;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.servlet.ClassloaderServlet;
import com.aopbuddy.servlet.EvalServlet;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;

@Slf4j
public class BootStrap {
    public static void start(String args, Instrumentation instrumentation) {
        try {
            Context.init(instrumentation);
            MethodPointcut pointcut = MethodPointcut.of(
                    "com.myth.earth.rmi.server.ConsoleRemoteServer", "*", "(..)");
            ExampleListener exampleListener = new ExampleListener();
            Context.registerAdvisor(pointcut, exampleListener);
            System.out.println("agentmain" + pointcut);
            System.out.println("agentmain" + exampleListener);
            HttpUtil.createServer(8888)
                    .addAction("/classloader", new ClassloaderServlet())
                    .addAction("/eval", new EvalServlet())
                    .start();
        } catch (Throwable e) {
            log.error("error",e);
            throw new RuntimeException(e);
        }

    }
}
