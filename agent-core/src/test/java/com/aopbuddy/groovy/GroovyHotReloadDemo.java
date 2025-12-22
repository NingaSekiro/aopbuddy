package com.aopbuddy.groovy;

import groovy.lang.GroovyShell;
import java.io.File;

public class GroovyHotReloadDemo {

    private static final String GROOVY_SCRIPT_PATH = "D:\\Code\\aopbuddy\\agent-core\\src\\test\\java\\com\\aopbuddy\\groovy\\MockListener.groovy";

    // 每次调用都重新解析脚本文件，实现热更新
    public static String callGroovyMethod(String name) throws Exception {
        GroovyShell shell = new GroovyShell();
        File scriptFile = new File(GROOVY_SCRIPT_PATH);

        // 解析脚本文件并获取Class对象
        Class<?> scriptClass = shell.getClassLoader().parseClass(scriptFile);

        // 创建Listener实例
        Object listenerInstance = scriptClass.getDeclaredConstructor().newInstance();

        // 获取after方法并调用
        java.lang.reflect.Method afterMethod = scriptClass.getMethod("after",
            Object.class, Class.class, com.aopbuddy.bytekit.MethodInfo.class, Object[].class, Object.class);

        // 创建模拟参数
        Object target = new Object();
        Class<?> clazz = Object.class;
        com.aopbuddy.bytekit.MethodInfo methodInfo = null; // 简化处理，实际使用时需要正确构造
        Object[] args = new Object[]{name};
        Object returnValue = "mockReturnValue";

        // 调用after方法并返回结果
        Object result = afterMethod.invoke(listenerInstance, target, clazz, methodInfo, args, returnValue);
        return result != null ? result.toString() : "";
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            try {
                String result = callGroovyMethod("World");
                System.out.println("当前调用结果: " + result);
            } catch (Exception e) {
                System.out.println("调用出错: " + e.getMessage());
            }

            // 每 3 秒调用一次，期间你可以手动修改 MockListener.groovy 文件
            Thread.sleep(3000);
        }
    }
}