package com.aopbuddy.retransform;

import com.aopbuddy.aspect.ClassObject;
import com.aopbuddy.infrastructure.TypeElementMatcher;
import lombok.Setter;
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

    public static Instrumentation inst ;

//    static {
//        init(null);
//    }

    public static void init(Instrumentation instrumentation) {
        if (instrumentation != null) {
            inst = instrumentation;
        }
        ClassFileTransformer buddyTransformer2 = new AgentBuilder
                .Default()
//                重新 inject 了同一个类
                .ignore(getDefaultIgnore())
                .disableClassFormatChanges()
                .type(ElementMatchers.not((ElementMatchers.isSynthetic())
                        .or(TypeDescription::isAnonymousType)
                ).and(new TypeElementMatcher()))
                .transform(new PointcutTransformer())
                .makeRaw();
        inst.addTransformer(buddyTransformer2, true);
    }


    public static void registerAdvisor(Pointcut pointcut, Listener listener) {
        ADVISORS.add(new Advisor(pointcut, listener));
        weave();
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
    private static void weave() {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        List<Class> classes = Arrays.stream(allLoadedClasses)
                .filter(clz -> filter(clz)).collect(Collectors.toList());
        System.out.println("classes: " + classes);
        if (classes.isEmpty()) {
            System.out.println("empty poincut");
            return;
        }
        inst.retransformClasses(classes.toArray(new Class[0]));
    }


    private static boolean filter(Class<?> clz) {
        for (Advisor advisor : Context.ADVISORS) {
            if (advisor.getPointcut().matches(new ClassObject.ForClass(clz))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 默认忽视包
     */
    private static ElementMatcher.Junction<? super TypeDescription> getDefaultIgnore() {
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
