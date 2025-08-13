package com.aopbuddy.aspect;

/**
 * @author lipan
 * @since 2025-01-22
 */
public interface CustomPointcutExpression {
    default boolean fastMatchType(String name) {
        return false;
    }

    boolean couldMatchJoinPointsInType(String name);

    boolean matchesMethodExecution(String owner, String methodName, String methodDesc);

    String getPointcutExpression();
}
