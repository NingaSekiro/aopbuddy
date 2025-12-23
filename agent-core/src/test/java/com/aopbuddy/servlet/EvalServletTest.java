package com.aopbuddy.servlet;

import com.aopbuddy.groovy.ConsoleScript;
import com.aopbuddy.infrastructure.JsonUtil;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Test;

class EvalServletTest {
    @Test
    void doGet() {
        // 配置脚本基类
        CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(ConsoleScript.class.getName());

        // 使用GroovyClassLoader创建GroovyShell
        GroovyShell groovyShell = new GroovyShell(config);

        // 执行脚本
        Object result = groovyShell.evaluate("return System.currentTimeMillis()");
        String json = JsonUtil.toJson(result);
        System.out.println(json);
    }

}