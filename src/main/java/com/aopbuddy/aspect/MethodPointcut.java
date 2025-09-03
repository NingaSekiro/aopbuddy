package com.aopbuddy.aspect;


import com.aopbuddy.infrastructure.MethodSignature;
import com.aopbuddy.retransform.Pointcut;

import java.lang.reflect.Method;

public class MethodPointcut extends Pointcut {
    /**
     * 支持通配符
     * com..MyBean
     * com.*.MyBean
     */
    private final String className;

    private final MethodSignature methodSignature;

    protected MethodPointcut(String className, MethodSignature methodSignature) {
        super(MethodSignature.getSignature(className, methodSignature));
        if (className == null || className.length() == 0) {
            throw new IllegalArgumentException("className is null");
        }
        this.className = className;
        this.methodSignature = methodSignature;
    }

    public MethodPointcut(String className, String methodName, String methodDesc) {
        this(className, new MethodSignature(methodName, methodDesc));
    }

    public String getClassName() {
        return className;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    /*
    public PointcutParser getParser(){
        return PointcutParser.of(this);
    }
     */
    public static MethodPointcut of(Method method) {
        return new MethodPointcut(method.getDeclaringClass().getName(), new MethodSignature(method));
    }

    public static MethodPointcut of(String className, String methodName, String methodDesc) {
        return new MethodPointcut(className, new MethodSignature(methodName, methodDesc));
    }

    @Override
    public String toString() {
        return "MethodPointcut{" + className + "#" + methodSignature + "_" + hashCode();
    }

    @Override
    public boolean matches(ClassObject clz) {
        return PointcutParser.of(this).isClass(clz.getName());
    }

    @Override
    public boolean matches(MethodObject method) {

        if (!PointcutParser.of(this).isMethodName(method.getName())) {
            return false;
        }

        String descriptor = methodSignature.getDescriptor();
        if (descriptor.startsWith("(..)")) {
            return true;
        }

        return is(method.getDescriptor(), descriptor);
    }

    public static boolean is(String descriptor1, String descriptor2) {
        String descriptor11 = descriptor1.substring(0, descriptor1.lastIndexOf(')'));
        String descriptor22 = descriptor2.substring(0, descriptor2.lastIndexOf(')'));
        return descriptor11.equals(descriptor22);
    }
}
