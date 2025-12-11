package com.aopbuddy.bytekit;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.asm.location.Location;
import com.alibaba.bytekit.asm.location.LocationType;
import com.alibaba.bytekit.asm.location.filter.GroupLocationFilter;
import com.alibaba.bytekit.asm.location.filter.InvokeContainLocationFilter;
import com.alibaba.bytekit.asm.location.filter.LocationFilter;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.aopbuddy.retransform.Advisor;
import com.aopbuddy.retransform.Context;
import com.taobao.arthas.common.FileUtils;

import java.aopbuddy.SpyAPI;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.aopbuddy.retransform.Context.*;


public class Enhancer implements ClassFileTransformer {

    public static final Logger LOGGER = Logger.getLogger(Enhancer.class.getName());
    private static SpyImpl spyImpl = new SpyImpl();

    static {
        SpyAPI.setSpy(spyImpl);
    }

    @Override
    public byte[] transform(ClassLoader inClassLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (inClassLoader != null) {
                inClassLoader.loadClass(SpyAPI.class.getName());
            }
        } catch (Throwable e) {
            return classfileBuffer;
        }
        if (className == null) {
            return classfileBuffer;
        }
        try {
            final String normalizedClassName = className.replace('/', '.');
            if (isIgnore(normalizedClassName)) {
                return classfileBuffer;
            }
            List<Advisor> filteredAdvisors = Context.ADVISORS.stream().filter(advisor -> advisor.getPointcut().matchesClassName(normalizedClassName)).
                    collect(Collectors.toList());
            if (filteredAdvisors.isEmpty()) {
                return classfileBuffer;
            }

            //keep origin class reader for bytecode optimizations, avoiding JVM metaspace OOM.
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
            // remove JSR https://github.com/alibaba/arthas/issues/1304
            classNode = AsmUtils.removeJSRInstructions(classNode);

            // 生成增强字节码
            DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();
            final List<InterceptorProcessor> interceptorProcessors = new ArrayList<>();
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor1.class));
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor2.class));
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor3.class));

            // 用于检查是否已插入了 spy函数，如果已有则不重复处理
            GroupLocationFilter groupLocationFilter = new GroupLocationFilter();

            LocationFilter enterFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atEnter",
                    LocationType.ENTER);
            LocationFilter existFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atExit",
                    LocationType.EXIT);
            LocationFilter exceptionFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class),
                    "atExceptionExit", LocationType.EXCEPTION_EXIT);

            groupLocationFilter.addFilter(enterFilter);
            groupLocationFilter.addFilter(existFilter);
            groupLocationFilter.addFilter(exceptionFilter);

            List<MethodNode> matchedMethods = new ArrayList<>();
            for (MethodNode methodNode : classNode.methods) {
                if (!isIgnore(methodNode)) {
                    for (Advisor filteredAdvisor : filteredAdvisors) {
                        matchedMethods.add(methodNode);
                        filteredAdvisor.addSignature(key(normalizedClassName, methodNode.name));
                    }
                }
            }
            for (MethodNode methodNode : matchedMethods) {
                if (AsmUtils.isNative(methodNode)) {
                    continue;
                }
                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode, groupLocationFilter);
                for (InterceptorProcessor interceptor : interceptorProcessors) {
                    try {
                        List<Location> locations = interceptor.process(methodProcessor);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            byte[] enhanceClassByteArray = AsmUtils.toBytes(classNode, inClassLoader, classReader);
//            dumpClassIfNecessary(className, enhanceClassByteArray);
            return enhanceClassByteArray;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE
                    , "Enhancer ERROR ", e);
            throw new RuntimeException(e);
        }
    }

    private static void dumpClassIfNecessary(String className, byte[] data) {
        final File dumpClassFile = new File("./arthas-class-dump/" + className + ".class");
        final File classPath = new File(dumpClassFile.getParent());

        // 创建类所在的包路径
        if (!classPath.mkdirs() && !classPath.exists()) {
            return;
        }

        // 将类字节码写入文件
        try {
            FileUtils.writeByteArrayToFile(dumpClassFile, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}