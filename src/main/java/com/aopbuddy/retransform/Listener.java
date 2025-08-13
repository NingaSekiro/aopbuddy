package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.MockedReturnValue;

import java.lang.reflect.Method;

public interface Listener {
    void before(Object target, Method method, Object[] args);

    MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue);

    void onException(Object target, Method method, Object[] args, Throwable throwable);
}
