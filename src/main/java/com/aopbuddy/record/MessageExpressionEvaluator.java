package com.aopbuddy.record;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息表达式求值器
 * 支持 ${[index].methodName()} 格式的表达式
 */
public class MessageExpressionEvaluator {
    
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    /**
     * 执行表达式，返回结果字符串
     */
    public static String evaluate(String expression, Object[] args) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }
        if (args == null) {
            args = new Object[0];
        }
        
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String value = evaluateExpression(matcher.group(1).trim(), args);
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value : "null"));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * 执行单个表达式：[index].method1().method2()...
     */
    private static String evaluateExpression(String expr, Object[] args) {
        try {
            if (!expr.startsWith("[")) {
                return expr;
            }
            
            int indexEnd = expr.indexOf(']');
            if (indexEnd < 0) {
                return expr;
            }
            
            int index;
            try {
                index = Integer.parseInt(expr.substring(1, indexEnd));
            } catch (NumberFormatException e) {
                return expr;
            }
            
            if (index < 0 || index >= args.length || args[index] == null) {
                return "null";
            }
            
            String methodChain = expr.substring(indexEnd + 1).trim();
            if (methodChain.isEmpty()) {
                return String.valueOf(args[index]);
            }
            
            Object result = executeMethodChain(args[index], methodChain);
            return result != null ? String.valueOf(result) : "null";
            
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * 执行方法调用链：.method1().method2()...
     */
    private static Object executeMethodChain(Object obj, String methodChain) throws Exception {
        Object current = obj;
        List<String> methods = parseMethodChain(methodChain);
        
        for (String methodCall : methods) {
            if (current == null) {
                return null;
            }
            
            String methodName = methodCall.substring(1, methodCall.indexOf('('));
            Method method = findMethod(current.getClass(), methodName);
            if (method == null) {
                throw new NoSuchMethodException("Method not found: " + methodName);
            }
            
            method.setAccessible(true);
            current = method.invoke(current);
        }
        
        return current;
    }
    
    /**
     * 解析方法调用链：.getMethod().toString() -> [".getMethod()", ".toString()"]
     */
    private static List<String> parseMethodChain(String methodChain) {
        List<String> methods = new ArrayList<>();
        if (methodChain == null || !methodChain.startsWith(".")) {
            return methods;
        }
        
        int methodStart = 0;
        int depth = 0;
        
        for (int i = 0; i < methodChain.length(); i++) {
            char c = methodChain.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    methods.add(methodChain.substring(methodStart, i + 1));
                    int nextStart = i + 1;
                    if (nextStart < methodChain.length() && methodChain.charAt(nextStart) == '.') {
                        methodStart = nextStart;
                    } else {
                        break;
                    }
                }
            }
        }
        
        return methods;
    }
    
    /**
     * 查找无参数方法
     */
    private static Method findMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                    return method;
                }
            }
        }
        return null;
    }
}

