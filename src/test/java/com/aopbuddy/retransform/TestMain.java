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
        TargetService svc = new TargetService();
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.TargetService", "greet", "(..)");
        Listener listener = new MethodListener();
        Context.registerAdvisor(pointcut, listener);
        String again = svc.greet("again");
        assertEquals("mocked", again);
    }
}
