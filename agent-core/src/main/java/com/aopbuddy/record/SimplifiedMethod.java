package com.aopbuddy.record;

/**
 * 简化后的方法信息数据类
 */
public class SimplifiedMethod {
    private final String returnSimpleClassName;
    private final String simpleTargetClassName;
    private final String methodName;

    public SimplifiedMethod(String returnSimpleClassName,
                            String simpleTargetClassName, String methodName) {
        this.returnSimpleClassName = returnSimpleClassName;
        this.simpleTargetClassName = simpleTargetClassName;
        this.methodName = methodName;
    }


    public String getReturnSimpleClassName() {
        return returnSimpleClassName;
    }

    public String getSimpleTargetClassName() {
        return simpleTargetClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String toString() {
        return returnSimpleClassName + " " + simpleTargetClassName + " " + methodName;
    }
}