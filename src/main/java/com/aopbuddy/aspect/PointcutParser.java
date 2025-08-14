package com.aopbuddy.aspect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PointcutParser {
    private static final ConcurrentHashMap<String, PointcutParser> cache = new ConcurrentHashMap<>();

    private final Pattern classPattern;

    private final Pattern methodNamePattern;


    public PointcutParser(String className, String methodName, String methodDesc) {

        classPattern = Pattern.compile(getClassRegex(className));
        methodNamePattern = Pattern.compile(getMethodRegex(methodName));
    }

    public Pattern getClassPattern() {
        return classPattern;
    }

    public boolean isMethodName(String methodName) {
        return methodNamePattern.matcher(methodName).matches();
    }

    public boolean isClass(String className) {
        return classPattern.matcher(className).matches();
    }

    private static String getClassRegex(String className) {
        String regex = className.replaceAll("\\*", "[a-zA-Z0-9_\\$]*")
                .replaceAll("\\.\\.", "\\.([a-zA-Z0-9_\\$]+\\.)*")
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\$", "\\\\\\$")
                ;
        return regex;
    }

    private static String getMethodRegex(String methodName) {
        String regex = methodName.replaceAll("\\*", "[a-zA-Z0-9_\\$]*");
        return regex;
    }

    //    public static PointcutParser of(String expression) {
//        PointcutParser pointcutParser = cache.get(expression);
//        if (pointcutParser == null) {
//            pointcutParser = new PointcutParser(expression);
//            cache.put(expression, pointcutParser);
//        }
//        return pointcutParser;
//    }
    public static PointcutParser of(MethodPointcut methodPointcut) {
        String key = methodPointcut.toString();
        PointcutParser pointcutParser = cache.get(key);
        if (pointcutParser == null) {
            pointcutParser = new PointcutParser(methodPointcut.getClassName()
                    , methodPointcut.getMethodSignature().getName()
                    , methodPointcut.getMethodSignature().getDescriptor());
            cache.put(key, pointcutParser);
        }
        return pointcutParser;
    }
/*
    public static PointcutParser of(String className, MethodSignature methodSignature) {
        String key=className+"#"+methodSignature;
        PointcutParser pointcutParser = cache.get(key);
        if (pointcutParser == null) {
            pointcutParser = new PointcutParser(className,methodSignature.getName(),methodSignature.getDescriptor());
            cache.put(key, pointcutParser);
        }
        return pointcutParser;
    }

 */
}
