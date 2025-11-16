package com.aopbuddy.aspect;

import lombok.Getter;

@Getter
public abstract class Pointcut {
    public abstract boolean matchesClassName(String className);

    public abstract boolean matchesMethodName(String methodName);

}
