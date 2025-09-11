package com.aopbuddy.servlet;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import cn.hutool.json.JSONUtil;
import com.aopbuddy.groovy.ConsoleScript;
import com.aopbuddy.groovy.EvalRequest;
import com.aopbuddy.groovy.GroovyConsoleLoader;
import com.aopbuddy.vmtool.ClassUtil;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.IOException;
import java.security.CodeSource;
import java.util.Optional;

@Slf4j
public class EvalServlet implements Action {
    @Override
    public void doAction(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) {
        EvalRequest evalRequest = JSONUtil.toBean(httpServerRequest.getBody(), EvalRequest.class);
        log.info("evalRequest: {}", evalRequest);
        Binding binding = new Binding();
        try {
            // 关键修改：使用GroovyClassLoader而不是Web应用类加载器
            // 这样可以确保Groovy AST转换相关类的可见性
            Optional<ClassLoader> first = ClassUtil.getClassLoaders().stream().filter(classLoader -> classLoader.getClass().getName().equals(evalRequest.getClassloader())).findFirst();
            GroovyConsoleLoader groovyClassLoader = new GroovyConsoleLoader(first.orElseGet(ClassUtil::getDefaultClassLoader));

            // 配置脚本基类
            CompilerConfiguration config = new CompilerConfiguration();
            config.setScriptBaseClass(ConsoleScript.class.getName());

            // 使用GroovyClassLoader创建GroovyShell
            GroovyShell groovyShell = new GroovyShell(groovyClassLoader, binding, config);

            // 执行脚本
            Object result = groovyShell.evaluate(evalRequest.getScript());

            // 将结果写入响应
            httpServerResponse.write(result != null ? result.toString() : "null");

        } catch (Exception e) {
            log.error("Groovy evaluation error", e);
            httpServerResponse.write("Error: " + e.getMessage());
        }
    }
}
