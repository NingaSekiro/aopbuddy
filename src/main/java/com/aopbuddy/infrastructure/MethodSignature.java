package com.aopbuddy.infrastructure;


import aj.org.objectweb.asm.Type;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Objects;


public class MethodSignature {
    /**
     * 支持通配符
     * add*
     * update*
     */
    private final String name;
    /**
     * 支持通配符
     * (..)
     * (II) 无返回值
     */
    private final String descriptor;

    public static final String POUND = "#";


    public MethodSignature(String name, String descriptor) {
        if (name.indexOf('(') >= 0) {
            throw new IllegalArgumentException("methodName '" + name + "' is invalid");
        }
        this.name = name;
        this.descriptor = descriptor;
    }

    public MethodSignature(String name, Class<?> rType, Class<?>... pTypes) {
        this(name, getDescriptor(rType, pTypes));
    }

    public static String getSignature(String className, MethodSignature methodSignature) {
        return className + POUND + methodSignature.toString();
    }

    public MethodSignature(Method method) {
        this(method.getName(), Type.getMethodDescriptor(method));
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public Type getReturnType() {
        return Type.getReturnType(descriptor);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(descriptor);
    }

    @Override
    public String toString() {
        return name + descriptor;
    }

    public static String getDescriptor(Class<?> rType, Class<?>... pTypes) {
        return MethodType.methodType(rType, pTypes).toMethodDescriptorString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MethodSignature && equals((MethodSignature) obj);
    }

    public boolean equals(MethodSignature other) {
        return name.equals(other.name) && Objects.equals(descriptor, other.descriptor);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ descriptor.hashCode();
    }
}
