package com.aopbuddy.agent;

import com.aopbuddy.infrastructure.ProcessUtils;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.util.InputMismatchException;


public class Attacher {
    /*
    * 1. 适配远程（完成）
    *  2. 适配jps(完成）
    * 4. 适配插件后台线程（完成）
    * 5. 考虑异常处理
    * 6. 考虑自带tools.jar(完成）
    * 7.前台的拉动条(改成画布）
    * 8.考虑多个listener匹配到的情况（已修复）
    * 切换页面是录制状态不变（已修复）
    * 录制前检查是否连上agent
    * 前端页面关闭时再打开的情况(关闭也要停止录制，已修复）
    * 对于统一入口而说效果不好（重点）
    * 录制状态有问题
    * 正在录制的状态界面展示还未完成
    * methodChain需要简化
    * weave日志和录制数据可以简化
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
