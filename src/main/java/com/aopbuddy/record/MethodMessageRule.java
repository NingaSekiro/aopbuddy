package com.aopbuddy.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 方法消息提取规则
 * 用于在特定类型和方法匹配时，通过表达式提取消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodMessageRule {
    /**
     * 目标类型（完整类名，如：jakarta.servlet.FilterChain）
     */
    private String type;

    /**
     * 方法名（如：doFilter）
     */
    private String method;

    /**
     * 消息提取表达式（如：${[0].getMethod()} ${[0].getRequestURI()}）
     * 支持格式：
     * - ${[index].methodName()} - 调用参数的某个方法
     * - ${[index].methodName().methodName2()} - 链式调用
     * - 普通文本 - 直接输出
     */
    private String messageExpression;

    /**
     * 检查是否匹配
     */
    public boolean matches(Set<String> typeNames, String methodName) {
        return typeNames.stream().anyMatch(typeName -> (type.equals(typeName)))
                && (method.equals(methodName));
    }
}

