package com.aopbuddy.servlet;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import com.aopbuddy.groovy.EvalRequest;
import com.aopbuddy.groovy.GroovyShellFactory;
import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.LoggerFactory;
import groovy.lang.GroovyShell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvalServlet implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvalServlet.class.getName(), LoggerFactory.LogFile.WEB);

    @Override
    public void doAction(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) {
        EvalRequest evalRequest = JsonUtil.parse(httpServerRequest.getBody(), EvalRequest.class);
        LOGGER.info("evalRequest: " + evalRequest);
        try {
            // 创建GroovyShell
            GroovyShell groovyShell = GroovyShellFactory.createGroovyShell(evalRequest.getClassloader());
            // 执行脚本
            Object result = groovyShell.evaluate(evalRequest.getScript());

            // 将结果写入响应
            httpServerResponse.write(result != null ? JsonUtil.toJson(result) : "null");
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING
                    , "EvalServlet ERROR ", e);
            StringWriter sw = new StringWriter(1024);
            PrintWriter pw = new PrintWriter(sw);
            pw.println(String.format("Error: %s: %s", e.getClass().getName(), String.valueOf(e.getMessage())));
            e.printStackTrace(pw);
            pw.flush();
            httpServerResponse.write(sw.toString());
        }
    }
}
