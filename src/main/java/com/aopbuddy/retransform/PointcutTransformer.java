package com.aopbuddy.retransform;


import com.aopbuddy.infrastructure.MethodChecker;
import lombok.SneakyThrows;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PointcutTransformer implements AgentBuilder.Transformer {
    public static final Set<String> BLACK_METHOD_NAMES = Collections.unmodifiableSet(new HashSet<String>() {{
        add("equals");
        add("hashCode");
        add("toString");
    }});

    @SneakyThrows
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
        List<Advisor> advisors = Context.ADVISORS;
        List<Advisor> filteredAdvisors = advisors.stream().filter(advisor -> advisor.getPointcut().matchesClassName(typeDescription.getName())).
                collect(Collectors.toList());
        if (filteredAdvisors.isEmpty()) {
            return builder;
        }
        MethodList<MethodDescription.InDefinedShape> methods = typeDescription.getDeclaredMethods()
                .filter(ElementMatchers.isMethod()
                        .and(ElementMatchers.not(ElementMatchers.isAbstract()
                                .or(ElementMatchers.isNative()
                                )))
                );
        for (MethodDescription.InDefinedShape methodDescription : methods) {
            if (BLACK_METHOD_NAMES.contains(methodDescription.getActualName())) {
                continue;
            }
            if (MethodChecker.isGetter(methodDescription) || MethodChecker.isSetter(methodDescription)) {
                continue;
            }
            for (Advisor advisor : filteredAdvisors) {
                if (advisor.getPointcut().matchesMethodName(methodDescription.getActualName())) {
                    builder = event(builder, methodDescription);
                    advisor.addSignature(Context.key(typeDescription.getName(), methodDescription.getActualName()));
                }
            }
        }
        return builder;
    }

    private DynamicType.Builder<?> event(DynamicType.Builder<?> builder, MethodDescription methodDescription) {
        return builder.visit(Advice.to(ListenerAdvice.class).on(methodDescription::equals));
    }
}

