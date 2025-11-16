package com.aopbuddy.infrastructure;

import net.bytebuddy.description.method.MethodDescription;

public class MethodChecker {

    /**
     * 判断是否为 Getter 方法
     */
    public static boolean isGetter(MethodDescription method) {
        String name = method.getName();
        if (!name.startsWith("get") && !name.startsWith("is")) {
            return false;
        }
        if (!method.getParameters().isEmpty() || "void".equals(method.getReturnType().getTypeName())) {
            return false;
        }
        // 可选：检查驼峰命名（首字母后大写）
        int start = name.startsWith("get") ? 3 : 2;
        if (name.length() > start && !Character.isUpperCase(name.charAt(start))) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为 Setter 方法
     */
    public static boolean isSetter(MethodDescription method) {
        String name = method.getName();
        if (!name.startsWith("set") || name.length() <= 3) {
            return false;
        }
        if (method.getParameters().size() != 1 || !"void".equals(method.getReturnType().getTypeName())) {
            return false;
        }
        // 可选：检查驼峰命名
        if (!Character.isUpperCase(name.charAt(3))) {
            return false;
        }
        return true;
    }
}