package com.aopbuddy.servlet;

import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import cn.hutool.json.JSONUtil;
import com.aopbuddy.vmtool.ClassUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClassloaderServlet implements Action {
    @Override
    public void doAction(HttpServerRequest httpServerRequest, HttpServerResponse httpServerResponse) throws IOException {
        List<String> classloaderNames = ClassUtil.getClassLoaders().stream().map(classLoader -> classLoader.getClass().getName()).collect(Collectors.toList());
        httpServerResponse.write(JSONUtil.toJsonStr(classloaderNames));
    }
}
