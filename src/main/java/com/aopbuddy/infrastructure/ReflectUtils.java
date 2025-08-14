package com.aopbuddy.infrastructure;

import java.lang.reflect.Method;

/**
 * @author lipan
 * @since 2025-01-22
 */
public abstract class ReflectUtils {
    public static Method findDeclaredMethod(Class<?> target, String methodName, Class<?>... pType) {
        try {
            return target.getDeclaredMethod(methodName, pType);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static Class<?> findClass(ClassLoader classLoader, String className) {
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
