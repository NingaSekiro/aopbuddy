package com.aopbuddy.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * @ IDE    ：IntelliJ IDEA.
 * @ Author ：dahuoyzs
 * @ Date   ：2020/5/23  15:48
 * @ Desc   ：
 */
public class Demo4 {

    public static void main(String[] args) throws Exception {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (int i = 0; i < list.size(); i++) {
            VirtualMachineDescriptor jvm = list.get(i);
            System.out.println("[" + i + "]ID:" + jvm.id() + ",Name:" + jvm.displayName());
        }
        System.out.println("请选择第几个");
        Scanner scanner = new Scanner(System.in);
        int s = scanner.nextInt();
        VirtualMachineDescriptor virtualMachineDescriptor = list.get(s);
        VirtualMachine attach = VirtualMachine.attach(virtualMachineDescriptor.id());
        String agentJar = "D:\\Code\\aopbuddy\\target\\aopbuddy-1.0-jar-with-dependencies.jar";
        File file = new File(agentJar);
        System.out.println(agentJar);
        System.out.println(file.exists());
        try {
            attach.loadAgent(agentJar, "param");
        } finally {
            attach.detach();
        }
    }
//
}
