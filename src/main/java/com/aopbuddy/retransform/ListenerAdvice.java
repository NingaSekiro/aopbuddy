package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.infrastructure.ReflectMethodChecker;
import lombok.SneakyThrows;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.*;


/**
 * Advice: 在方法 enter/exit/throw 时调用 AgentBridge 通知 listeners
 * <p>
 * 注意：
 * - @Advice.This(optional = true) 支持静态方法 this 为 null
 * - @Advice.Origin("#t") / "#m" 用于获取类名和方法名
 */
public class ListenerAdvice {

    public static final Set<String> BLACK_METHOD_NAMES = Collections.unmodifiableSet(new HashSet<String>() {{
        add("equals");
        add("hashCode");
        add("toString");
    }});

    @SneakyThrows
    @Advice.OnMethodEnter
    public static EnterResult onEnter(@Advice.This(optional = true) Object thiz,
                                      @Advice.Origin("#t") String className,
                                      @Advice.Origin("#m") String methodName,
                                      @Advice.Origin("#d") String methodDesc,
                                      @Advice.Origin("#s") String signature,
                                      @Advice.Origin Method method,
                                      @Advice.AllArguments Object[] args) {
        List<Listener> listeners = new ArrayList<>();
        for (Advisor advisor : Context.ADVISORS) {
            if (ReflectMethodChecker.isGetter(method) || ReflectMethodChecker.isSetter(method) || !advisor.getPointcut().matchesClassName(className)) {
                continue;
            }
            if (!BLACK_METHOD_NAMES.contains(methodName) && advisor.getPointcut().matchesMethodName(methodName)) {
                listeners.add(advisor.getListener());
            }
        }
        // attempt to resolve Method by signature (AgentBridge knows parameter type names)
        for (Listener l : listeners) {
            l.before(thiz, method, args);
        }
        EnterResult enterResult = new EnterResult();
        enterResult.setListeners(listeners);
        return enterResult;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(@Advice.This(optional = true) Object thiz,
                              @Advice.Origin("#t") String className,
                              @Advice.Origin("#m") String methodName,
                              @Advice.Origin("#d") String methodDesc,
                              @Advice.Origin Method method,
                              @Advice.AllArguments Object[] args,
                              @Advice.Return(typing = Assigner.Typing.DYNAMIC, readOnly = false) Object returned,
                              @Advice.Thrown Throwable thrown,
                              @Advice.Enter EnterResult enterResult) {
        List<Listener> listeners = enterResult.getListeners();
        if (thrown != null) {
            for (Listener l : listeners) {
                l.onException(thiz, method, args, thrown);
            }
        } else {
            for (Listener l : listeners) {
                MockedReturnValue after = l.after(thiz, method, args, returned);
                if (after != null && after.isMock()) {
                    returned = after.getMockValue();
                }
            }
        }
    }

    public static Method getMethod(String className, String methodName, String methodDesc) throws ClassNotFoundException, NoSuchMethodException {
        // 加载类
        Class<?> clazz = Class.forName(className);

        // 解析方法描述符，获取参数类型
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        Class<?>[] parameterTypes = new Class<?>[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            parameterTypes[i] = Class.forName(argumentTypes[i].getClassName());
        }

        // 获取 Method 对象
        return clazz.getDeclaredMethod(methodName, parameterTypes);
    }

}
