package com.aopbuddy.retransform;


import com.aopbuddy.bytekit.MethodInfo;

public interface Listener {
    //
    void before(Object target, Class<?> clazz, MethodInfo methodInfo, String[] argNames, Object[] args);

    Object after(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args, Object returnValue);

    void onException(Object target, Class<?> clazz, MethodInfo methodInfo,Object[] args, Throwable throwable);
}
