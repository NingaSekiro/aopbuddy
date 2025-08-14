package com.aopbuddy.infrastructure;


import aj.org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * @author lipan
 * @since 2025-02-04
 */
public abstract class PointcutUtils {
    public static final String POUND = "#";

    public static String getSignature(Method method) {

        return method.getDeclaringClass().getName() + POUND + method.getName() + Type.getMethodDescriptor(method);
    }


    public static String getSignature(String className, String methodSignature) {
        return className + POUND + methodSignature;
    }

    public static String getSignature(String className, MethodSignature methodSignature) {
        return className + POUND + methodSignature.toString();
    }

    public static String getSignature(String className, String methodName, String methodDesc) {
        return className + POUND + getMethodSignature(methodName, methodDesc);
    }

    public static String getMethodSignature(String methodName, String methodDesc) {
        return methodName + methodDesc;
    }

    // public static String getSignature(MethodDescription methodDescription) {
    //     return getSignature(methodDescription.getDeclaringType().getActualName(), methodDescription.getName(),
    //             methodDescription.getDescriptor());
    // }

    public static String parseClassName(String signature) {
        return signature.substring(0, signature.indexOf(POUND));
    }

    public static Class<?> parseClass(ClassLoader classLoader, String signature) {
        return ReflectUtils.findClass(classLoader, parseClassName(signature));
    }

    public static MethodSignature parseMethodSignature(String signature) {
        String methodSignature = signature.substring(signature.indexOf(POUND) + 1);
        int left = methodSignature.indexOf('(');
        return new MethodSignature(methodSignature.substring(0, left), methodSignature.substring(left));
    }
}
