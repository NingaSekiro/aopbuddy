//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aopbuddy.groovy;

import cn.hutool.json.JSONUtil;
import com.aopbuddy.agent.ExampleListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.vmtool.ClassUtil;
import com.aopbuddy.vmtool.VmToolCommand;
import groovy.lang.Script;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public abstract class ConsoleScript extends Script {

    public Object[] get(Class<?> cla) {
        return this.get(cla, 10);
    }


    public Object getObject(Class<?> cla) {
        Object[] obj = this.get(cla, 10);
        return obj != null && obj.length > 0 ? obj[0] : obj;
    }

    public Object[] get(Class<?> cla, Integer limit) {
        limit = limit == null ? 10 : limit;
        return VmToolCommand.vmToolInstance().getInstances(cla, limit);
    }

    public String addListener(String className, String methodName) {
        MethodPointcut pointcut = MethodPointcut.of(
                className, methodName, "(..)");
        Context.registerAdvisor(pointcut, new ExampleListener());
        return "ok";
    }


    public String toJson(Object value) {
        return JSONUtil.toJsonStr(value);
    }

    public <T> T jsonToObj(String text, Class<T> clazz) {
        return JSONUtil.toBean(text, clazz);
    }

    public String readFile(String filePath) {
        return this.readFile(filePath, StandardCharsets.UTF_8);
    }

    public String readFile(String filePath, Charset charset) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }

            try {
                byte[] data = Files.readAllBytes(Paths.get(filePath));
                return new String(data, charset);
            } catch (IOException var4) {
                IOException e = var4;
                throw new RuntimeException("读取文件 " + filePath + " 失败: " + e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("文件路径不能为空");
        }
    }
}
