package com.aopbuddy.aspect;

import lombok.Getter;

import java.util.Objects;

@Getter
public abstract class Pointcut {
    protected final String expression;

    public Pointcut(String expression) {
        this.expression = expression;
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof Pointcut && equals((Pointcut) obj);
    }

    public final boolean equals(Pointcut pointcut) {
        return Objects.equals(expression, pointcut.expression);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(expression);
    }

    public abstract boolean matchesClassName(String className);

    public abstract boolean matchesMethodName(String methodName);

}
