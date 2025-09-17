package com.aopbuddy.retransform;


import com.aopbuddy.aspect.MethodObject;
import com.aopbuddy.aspect.Pointcut;
import lombok.SneakyThrows;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;

public class PointcutTransformer implements AgentBuilder.Transformer {
    @SneakyThrows
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        System.out.println("startstartstartstartstart");
        List<Advisor> advisors = Context.ADVISORS;
        List<Pointcut> pointcuts = advisors.stream().map(advisor -> advisor.getPointcut()).filter(pointcut -> pointcut.matchesClassName(typeDescription.getName())).
                collect(Collectors.toList());
        if (pointcuts.isEmpty()) {
            return builder;
        }
        MethodList<MethodDescription.InDefinedShape> methods = typeDescription.getDeclaredMethods()
                .filter(ElementMatchers.isMethod()
                        .and(ElementMatchers.not(ElementMatchers.isAbstract()
                                .or(ElementMatchers.isNative()
                                )))
                );
        for (MethodDescription.InDefinedShape methodDescription : methods) {
            for (Pointcut pointcut : pointcuts) {
                if (pointcut.matchesMethodName(methodDescription.getActualName())) {
                    System.out.println("pointcut matched" + methodDescription.getActualName() + pointcut.hashCode());
                    builder = event(builder, methodDescription);
                    break;
                }
            }
        }
        return builder;
    }

    private DynamicType.Builder<?> event(DynamicType.Builder<?> builder, MethodDescription methodDescription) {
        return builder.visit(Advice.to(ListenerAdvice.class).on(methodDescription::equals));
    }
}

