package com.aopbuddy.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.apache.groovy.parser.antlr4.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Attacher {

    public static void main(String[] args) throws Exception {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        int s = -1;
        for (int i = 0; i < list.size(); i++) {
            VirtualMachineDescriptor jvm = list.get(i);
            String lowerCase = jvm.displayName().toLowerCase(Locale.ROOT);
            System.out.println("[" + i + "]ID:" + jvm.id() + ",Name:" + jvm.displayName());
            if (lowerCase.contains("spring") || lowerCase.contains("catalina")) {
                s = i;
            }
        }
        VirtualMachineDescriptor virtualMachineDescriptor = list.get(s);
        VirtualMachine attach = VirtualMachine.attach(virtualMachineDescriptor.id());
        File classFile = new File(Agent.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(classFile.getParent(), "/aopbuddy-1.0-jar-with-dependencies.jar");
        System.out.println(file.getAbsolutePath());
        System.out.println(file.exists());
        try {
            attach.loadAgent(file.getAbsolutePath());
        } finally {
            attach.detach();
        }
    }
//
}
