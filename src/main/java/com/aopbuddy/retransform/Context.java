package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.TypeElementMatcher;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import java.util.*;
import java.util.stream.Collectors;

public final class Context {
    public static final List<Advisor> ADVISORS = Collections.synchronizedList(new ArrayList<>());
    public static Instrumentation inst;

    // 这里在java agent下可能有问题
//    static {
//        init(null);
//    }

    public static void init(Instrumentation instrumentation) {
        // inst只维持一份
        if (inst == null && instrumentation != null) {
            inst = instrumentation;
        } else if (inst == null) {
            inst = ByteBuddyAgent.install();
        }
        // 只有第一次才会添加ClassFileTransformer，防止后面添加，中间层用ADVISORS来动态控制
        if (TransformerController.buddyTransformer == null) {
            ClassFileTransformer buddyTransformer2 = new AgentBuilder
                    .Default()
                    .disableClassFormatChanges()
//                重新 inject 了同一个类
                    .with(new DebugAgentListener())
                    .ignore(getDefaultIgnore())
                    .type(ElementMatchers.not((ElementMatchers.isSynthetic())
                            .or(TypeDescription::isAnonymousType)
                    ).and(new TypeElementMatcher()))
                    .transform(new PointcutTransformer())
                    .makeRaw();
            AbstractWeaver proxyWeaver = new AbstractWeaver();
            proxyWeaver.setTargetTransformer(buddyTransformer2);
            TransformerController.buddyTransformer = proxyWeaver;
            inst.addTransformer(proxyWeaver, true);
        }
    }


    public static void registerAdvisor(Pointcut pointcut, Listener listener) {
        ADVISORS.add(new Advisor(pointcut, listener));
        weave(pointcut);
    }

    public static void unregisterAdvisor(Pointcut pointcut, Class<? extends Listener> listenerClass) {
        Optional<Advisor> first = ADVISORS.stream()
                .filter(advisor -> advisor.getPointcut().equals(pointcut))
                .filter(advisor -> advisor.getListener().getClass().equals(listenerClass))
                .findFirst();
        if (first.isPresent()) {
            ADVISORS.remove(first.get());
        }
    }


    @SneakyThrows
    private static void weave(Pointcut pointcut) {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        List<Class> classes = Arrays.stream(allLoadedClasses)
                .filter(clz -> pointcut.matchesClassName(clz.getName())).collect(Collectors.toList());
        System.out.println("weave classes: " + classes);
        System.out.println("weave advisors" + ADVISORS);
        if (classes.isEmpty()) {
            System.out.println("empty poincut");
            return;
        }
        inst.retransformClasses(classes.toArray(new Class[0]));
    }


    public static boolean matchesClass(String className) {
        for (Advisor advisor : ADVISORS) {
            if (advisor.getPointcut().matchesClassName(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 默认忽视包
     */
    public static ElementMatcher.Junction<? super TypeDescription> getDefaultIgnore() {
        return ElementMatchers.nameStartsWith("java.")
                .or(ElementMatchers.nameStartsWith("jdk."))
                .or(ElementMatchers.nameStartsWith("javax."))
                .or(ElementMatchers.nameStartsWith("sun."))
                .or(ElementMatchers.nameStartsWith("com.sun."))
                .or(ElementMatchers.nameStartsWith("net.bytebuddy."))
                .or(ElementMatchers.nameStartsWith("org.aspectj."))
                .or(ElementMatchers.nameStartsWith("com.aopbuddy."))
                .or(ElementMatchers.isSynthetic());
    }
}
