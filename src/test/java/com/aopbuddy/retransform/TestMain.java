package com.aopbuddy.retransform;

import com.aopbuddy.agent.MethodListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddytest.TargetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMain {
    @AfterEach
    public void cleanup() {
        Context.ADVISORS.clear();
    }


    @Test
    public void addMethodPointcut() {
        Context.init(null);
        TargetService svc = new TargetService();
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.TargetService", "greetString", "(..)");
        Listener listener = new MethodListener();
        Context.registerAdvisor(pointcut, listener);
        String again = svc.greetString("again");
        assertEquals("mocked", again);
    }

    @Test
    public void deleteMethodPointcut() {
        Context.init(null);
        TargetService svc = new TargetService();
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.TargetService", "greetString", "(..)");
        Listener listener = new MethodListener();
        Context.registerAdvisor(pointcut, listener);
        String again = svc.greetString("again");
        assertEquals("mocked", again);
        Context.unregisterAdvisor(pointcut, MethodListener.class);
        again = svc.greetString("again");
        assertEquals("Num 1", again);
    }

}
