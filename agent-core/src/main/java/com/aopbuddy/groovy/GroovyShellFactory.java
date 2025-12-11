package com.aopbuddy.groovy;

import com.aopbuddy.vmtool.ClassUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyShellFactory {

    private static Map<String, GroovyShell> groovyShellMap = new ConcurrentHashMap<>();

    public static GroovyShell createGroovyShell(String classloader) {
        if (groovyShellMap.containsKey(classloader)) {
            return groovyShellMap.get(classloader);
        }
        Binding binding = new Binding();
        // 关键修改：使用GroovyClassLoader而不是Web应用类加载器
        // 这样可以确保Groovy AST转换相关类的可见性
        Optional<ClassLoader> first = ClassUtil.getClassLoaders().stream().filter(classLoader -> classLoader.getClass().getName().equals(classloader)).findFirst();
        GroovyConsoleLoader groovyClassLoader = new GroovyConsoleLoader(first.orElseGet(ClassUtil::getDefaultClassLoader));

        // 配置脚本基类
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(ConsoleScript.class.getName());

        // 使用GroovyClassLoader创建GroovyShell
        GroovyShell groovyShell = new GroovyShell(groovyClassLoader, binding, config);
        groovyShellMap.put(classloader, groovyShell);
        return groovyShell;
    }
}
