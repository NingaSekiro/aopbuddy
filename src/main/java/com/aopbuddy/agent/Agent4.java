package com.aopbuddy.agent;


import com.aopbuddy.aspect.AspectJPointcut;
import com.aopbuddy.retransform.Context;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;


@Slf4j
public class Agent4 {
    public static void agentmain(String agent, Instrumentation instrumentation) {
        System.out.println("Agent4 agentmain 2param :" + agent);
        try {
            Context.init(instrumentation);
            Context.registerAdvisor(new AspectJPointcut("execution(* com.example.springdemo.demos.web.controller.BasicController.*(..))"), new ExampleListener());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            log.error("Error in agentmain", e);
            throw new RuntimeException(e);
        }
    }
}
