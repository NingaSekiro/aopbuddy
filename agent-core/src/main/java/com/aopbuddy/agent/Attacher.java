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
     * 7.前台的拉动条(改成画布,完成）
     * 8.考虑多个listener匹配到的情况（已修复）
     * 切换页面是录制状态不变（已修复）
     * 录制前检查是否连上agent(已修复）
     * 对于统一入口比如filter而说效果不好（重点）首尾确定一条方法链
     * agent停止时前端要处理成未录制状态（已修复）
     * 录制状态有问题（已修复）
     * 正在录制的状态界面展示还未完成（已修复）
     * methodChain需要简化（已修复）
     * weave日志和录制数据可以简化(weave日志已去除，录制数据可以删除static字段）
     * 能否反向定位对象数据流向
     * 没有形参名称
     * 考虑类加载隔离，加个api桥接(对象注入，不加载类，完成)
     * 直接关闭浏览器也应该停止录制(完成)
     * 循环该如何减少录制数据？
     * private默认不展示，可以增量展示
     * 默认只展示本项目的首个方法（如果方法少，可以全部展示）(扩展时按照depth一级一级往下，或者往上）。depth临近的也不一定是父子关系
     * 循环判断：改为parent相同的方法相同的只保留两个。
     * - 父级方法的指纹 (Parent Signature)
        - 当前方法 ID (Current Method ID)
        - 调用点位置 (Call Site / Bytecode Index) —— 这是关键！
        *
        * sun.misc.Unsafe 直接读取内存
        *
        *
        * - 对象深度 ( xcodemap.obj.max ) : 默认为 4 层。超过深度的对象字段将不再展开。
- 集合大小 ( xcodemap.col.max ) : 默认为 32 。集合/数组最多只录制前 32 个元素。
- 字符串长度 ( xcodemap.string.max ) : 默认为 4096 字符。超长会被截断。
* nosql数据库，1.对象池已有对象2.没有对象但是为自己（循环引用）3.没有对象且不失循环引用则继续构造对象
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
            System.out.println("Selected pid: " + pid);
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
