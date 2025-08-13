package com.aopbuddy.retransform;

import com.aopbuddy.agent.ExampleListener;
import com.aopbuddy.aspect.AspectJPointcut;
import com.aopbuddytest.TargetService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMain {

    @Test
    public void addAndRemoveListener() {
        TargetService svc = new TargetService();
        Pointcut pc = new AspectJPointcut("execution(* com.aopbuddytest.TargetService.greet(java.lang.String))");
        Listener listener = new ExampleListener();
        Context.registerAdvisor(pc, listener);
        String again = svc.greet("again");
        assertEquals("mocked", again);
        Context.unregisterAdvisor(pc, ExampleListener.class);
        String two = svc.greet("again");
        assertEquals("Hello again", two);
    }
}
