package com.aopbuddy.retransform;


import com.aopbuddy.aspect.AspectJPointcut;
import com.aopbuddy.aspect.ClassObject;
import com.aopbuddy.aspect.MethodObject;
import com.aopbuddytest.Target;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AspectJPointcutTest {

    @Test
    void matchesMethod() {
        AspectJPointcut aspectJPointcut = new AspectJPointcut("execution(* com.aopbuddytest.Target.hello(java.lang.String))");
        boolean matches = aspectJPointcut.matches(new MethodObject.ForMethod(Target.class.getDeclaredMethods()[0]));
        assertEquals(true, matches);
    }

    @Test
    void matchesClass() {
        AspectJPointcut aspectJPointcut = new AspectJPointcut("execution(* com.aopbuddytest.Target.hello(java.lang.String))");
        boolean matches = aspectJPointcut.matches(new ClassObject.ForClass(Target.class));
        assertEquals(true, matches);
    }
}