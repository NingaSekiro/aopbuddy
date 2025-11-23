package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.MockedReturnValue;
import lombok.SneakyThrows;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.jar.asm.Type;

import java.lang.reflect.Method;
import java.util.List;


/**
 * Advice: 在方法 enter/exit/throw 时调用 AgentBridge 通知 listeners
 * <p>
 * 注意：
 * - @Advice.This(optional = true) 支持静态方法 this 为 null
 * - @Advice.Origin("#t") / "#m" 用于获取类名和方法名
 */
public class ListenerAdvice {


    @SneakyThrows
    @Advice.OnMethodEnter
    public static EnterResult onEnter(@Advice.This(optional = true) Object thiz,
                                      @Advice.Origin("#t") String className,
                                      @Advice.Origin("#m") String methodName,
                                      @Advice.Origin("#d") String methodDesc,
                                      @Advice.Origin("#s") String signature,
                                      @Advice.Origin Method method,
                                      @Advice.AllArguments Object[] args) {

        return new EnterResult();
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

    }


}
