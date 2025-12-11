package com.aopbuddy.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 方法消息规则管理器
 * 管理所有的方法消息提取规则
 */
public class MethodMessageRuleManager {

    private static final List<MethodMessageRule> rules = new CopyOnWriteArrayList<>();

    /**
     * 注册规则
     */
    public static void registerRule(MethodMessageRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    /**
     * 注册规则（便捷方法）
     */
    public static void registerRule(String type, String method, String messageExpression) {
        registerRule(new MethodMessageRule(type, method, messageExpression));
    }

    /**
     * 移除规则
     */
    public static void removeRule(MethodMessageRule rule) {
        rules.remove(rule);
    }

    /**
     * 清空所有规则
     */
    public static void clearRules() {
        rules.clear();
    }

    /**
     * 获取所有规则
     */
    public static List<MethodMessageRule> getRules() {
        return new ArrayList<>(rules);
    }

    /**
     * 查找匹配的规则
     */
    public static MethodMessageRule findMatchingRule(Set<String> typeNames, String methodName) {
        for (MethodMessageRule rule : rules) {
            if (rule.matches(typeNames, methodName)) {
                return rule;
            }
        }
        return null;
    }

    /**
     * 初始化默认规则
     */
    public static void initDefaultRules() {
        // 注册 jakarta.servlet.FilterChain.doFilter 的规则
        registerRule(
                "jakarta.servlet.FilterChain",
                "doFilter",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );

        // 注册 javax.servlet.FilterChain.doFilter 的规则（兼容旧版本）
        registerRule(
                "javax.servlet.FilterChain",
                "doFilter",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );
        registerRule(
                "javax.servlet.Servlet",
                "service",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );

        registerRule(
                "jakarta.servlet.Servlet",
                "getRequestURI",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );
        registerRule(
                "jakarta.servlet.Filter",
                "doFilter",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );

        registerRule(
                "javax.servlet.Filter",
                "doFilter",
                "${[0].getMethod()} ${[0].getRequestURI()}"
        );
    }

    static {
        // 自动初始化默认规则
        initDefaultRules();
    }
}

