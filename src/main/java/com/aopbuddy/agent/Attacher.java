package com.aopbuddy.agent;

import com.aopbuddy.infrastructure.ProcessUtils;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.util.InputMismatchException;


public class Attacher {
    //TODO:: 1.适配远程（完成） 2.适配jps(完成）3.适配listener日志（仿照arthas，日期淘汰，调用一次） 4.适配插件后台线程（完成）5.考虑异常处理6.考虑自带tools.jar 7.考虑定时清理ServerConfig
    /*
    * 1. 适配远程（完成）
    *  2. 适配jps(完成）
    * 3. 适配listener日志（仿照arthas，日期淘汰，调用一次）
    * 4. 适配插件后台线程（完成）
    * 5. 考虑异常处理
    * 6. 考虑自带tools.jar(完成）
    * 7. 考虑定时清理ServerConfig
    * 8.0 考虑H2数据库同步
    */

    public static void main(String[] args) throws Exception {
        String httpPort = "8888";
        if (args != null && args.length != 0) {
            httpPort = args[0];
        }
        long pid = -1;
        // select pid
        try {
            pid = ProcessUtils.select();
        } catch (InputMismatchException e) {
            System.out.println("Please input an integer to select pid.");
            System.exit(1);
        }
        if (pid < 0) {
            System.out.println("Please select an available pid.");
            System.exit(1);
        }
        boolean ready = ProcessUtils.addToolsJarToClasspath();
        if (!ready) {
            System.out.println("Can not find tools.jar, please make sure you are using a JDK instead of a JRE.");
            System.exit(1);
        }
        VirtualMachine attach = VirtualMachine.attach(Long.toString(pid));
        File classFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(classFile.getParent(), "/agent-jar-with-dependencies.jar");
        try {
            attach.loadAgent(file.getAbsolutePath(), httpPort);
        } finally {
            attach.detach();
        }
        System.out.println("finish");
    }
}
