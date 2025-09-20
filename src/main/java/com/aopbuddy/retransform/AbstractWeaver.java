package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.LoggerFactory;
import net.bytebuddy.jar.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

// 在byte-buddy动态加载类前加一层过滤。
public class AbstractWeaver implements ClassFileTransformer {
    public static final Logger LOGGER = LoggerFactory.getLogger("AbstractWeaver");

    private volatile ClassFileTransformer targetTransformer;

    public AbstractWeaver() {
    }

    public void setTargetTransformer(ClassFileTransformer targetTransformer) {
        this.targetTransformer = targetTransformer;
    }

    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> classBeingRedefined,
                            ProtectionDomain domain, byte[] classBytes) throws IllegalClassFormatException {
        try {
            // 如果不为空可以抛异常
            ClassFileTransformer transformer = this.targetTransformer;
            if (transformer == null || name == null) {
                return classBytes;
            }
            String className = name.replace('/', '.');
            if (skip(className) || !Context.matchesClass(className)) {
                return classBytes;
            }
            byte[] transform = transformer.transform(loader, name, classBeingRedefined, domain, classBytes);
            LOGGER.info("weaving " + className + " - "
//                    +transform.hashCode()
            );
            return transform;
        } catch (Throwable e) {

            LOGGER.log(Level.WARNING
                    , "weaving error " + name + " - ", e);
            throw new RuntimeException(e);

        }
    }


    private boolean skip(String className) {
        return className.startsWith("net.bytebuddy")
                || className.startsWith("java")
                || className.startsWith("jdk")
                || className.startsWith("org.aspectj.")
                || className.startsWith("com.aopbuddy.")
                || className.startsWith("sun");
    }


}
