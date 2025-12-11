package com.aopbuddy.retransform;

import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.aopbuddy.aspect.Pointcut;
import com.aopbuddy.bytekit.Enhancer;
import com.aopbuddy.infrastructure.ArthasCheckUtils;
import com.aopbuddy.infrastructure.LoggerFactory;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import java.lang.instrument.Instrumentation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class Context {

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class.getName());
    public static final List<Advisor> ADVISORS = Collections.synchronizedList(new ArrayList<>());
    public static Instrumentation inst;
    public static final ConcurrentHashMap<String, List<Listener>> CACHE = new ConcurrentHashMap<String, List<Listener>>();


    // 这里在java agent下可能有问题
//    static {
//        init(null);
//    }

    // DebugAgentListener->
    public static void init(Instrumentation instrumentation) {
        // inst只维持一份
        if (inst == null && instrumentation != null) {
            inst = instrumentation;
        } else if (inst == null) {
            inst = ByteBuddyAgent.install();
        }
        // 只有第一次才会添加ClassFileTransformer，防止后面添加，中间层用ADVISORS来动态控制
        //AbstractWeaver -> buddyTransformer2 -> PointcutTransformer
        if (TransformerController.buddyTransformer == null) {
            inst.addTransformer(new Enhancer(), true);
        }
    }


    public synchronized static void registerAdvisor(Pointcut pointcut, Listener listener) {
        Optional<Advisor> first = ADVISORS.stream()
                .filter(advisor -> advisor.getPointcut().equals(pointcut))
                .filter(advisor -> advisor.getListener().getClass().equals(listener.getClass()))
                .findFirst();
        if (first.isPresent()) {
            return;
        }
        ADVISORS.add(new Advisor(pointcut, listener));
        weave(pointcut);
    }

    public synchronized static void unregisterAdvisor(Pointcut pointcut, Class<? extends Listener> listenerClass) {
        Optional<Advisor> first = ADVISORS.stream()
                .filter(advisor -> advisor.getPointcut().equals(pointcut))
                .filter(advisor -> advisor.getListener().getClass().equals(listenerClass))
                .findFirst();
        if (first.isPresent()) {
            first.get().removeSignature();
            ADVISORS.remove(first.get());
        }
    }

    public synchronized static void unregisterAdvisorByListener(Class<? extends Listener> listenerClass) {
        ADVISORS.removeIf(advisor -> advisor.getListener().getClass().equals(listenerClass));
    }

    public synchronized static void addCache(String key, Listener listener) {
        List<Listener> listeners1 = CACHE.computeIfAbsent(key, k -> new ArrayList<>());
        listeners1.add(listener);
    }

    public synchronized static void deleteCache(Set<String> keys, Listener listener) {
        for (String key : keys) {
            List<Listener> listeners1 = CACHE.get(key);
            if (listeners1 == null) {
                return;
            }
            listeners1.removeIf(listener1 -> listener1 == listener);
        }
    }

    public static List<Listener> getCache(String signature) {
        return CACHE.get(signature);
    }

    public static String key(String className, String methodName) {
        return className + methodName;
    }

    @SneakyThrows
    private static void weave(Pointcut pointcut) {
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        List<Class> classes = Arrays.stream(allLoadedClasses)
                .filter(clz -> !isIgnore(clz.getName())
                        && pointcut.matchesClassName(clz.getName())
                )
                .collect(Collectors.toList());
        LOGGER.info("weave classes: " + classes);
        if (classes.isEmpty()) {
            LOGGER.info("empty poincut");
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

    public static boolean isIgnore(String className) {
        return className.startsWith("java.")
                || className.startsWith("jdk.")
                || className.startsWith("javax.")
                || className.startsWith("sun.")
                || className.startsWith("com.sun.")
                || className.startsWith("net.bytebuddy.")
                || className.startsWith("org.aspectj.")
                || className.startsWith("com.aopbuddy.")
                || className.contains("BySpringCGLIB$$");
    }

    /**
     * 是否需要忽略
     */
    public static boolean isIgnore(MethodNode methodNode) {
        return null == methodNode || isAbstract(methodNode.access)
                || ArthasCheckUtils.isEquals(methodNode.name, "<clinit>")
                || BLACK_METHOD_NAMES.contains(methodNode.name)
                || methodNode.name.contains("<init>")
                || isGetterOrSetter(methodNode);
    }

    /**
     * 是否抽象属性
     */
    private static boolean isAbstract(int access) {
        return (Opcodes.ACC_ABSTRACT & access) == Opcodes.ACC_ABSTRACT;
    }

    private static final Set<String> BLACK_METHOD_NAMES = Collections.unmodifiableSet(new HashSet<String>() {{
        add("equals");
        add("hashCode");
        add("toString");
        add("finalize");
        add("clone");
    }});

    private static boolean isGetterOrSetter(MethodNode methodNode) {
        String name = methodNode.name;
        Type[] argTypes = Type.getArgumentTypes(methodNode.desc);
        Type returnType = Type.getReturnType(methodNode.desc);

        // 检查是否为 getter（getName, isEnabled 等）
        // getter 特征：方法名以 get 开头，无参数，有返回值
        if (name.startsWith("get") && argTypes.length == 0
                && !returnType.equals(Type.VOID_TYPE)) {
            return true;
        }

        // 检查布尔类型的 getter（isEnabled, hasPermission 等）
        // boolean getter 特征：以 is 或 has 开头，无参数，返回 boolean
        if ((name.startsWith("is") || name.startsWith("has"))
                && argTypes.length == 0
                && returnType.equals(Type.BOOLEAN_TYPE)) {
            return true;
        }

        // 检查是否为 setter（setName 等）
        // setter 特征：方法名以 set 开头，1个参数，返回 void
        if (name.startsWith("set") && argTypes.length == 1
                && returnType.equals(Type.VOID_TYPE)) {
            return true;
        }

        return false;
    }
}
