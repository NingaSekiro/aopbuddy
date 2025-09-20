package com.aopbuddy.retransform;

import com.aopbuddy.infrastructure.LoggerFactory;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebugAgentListener implements AgentBuilder.Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger("DebugAgentListener");

    public static final String DEBUG_LOCATION_PROPERTY = "easy-aop.debugLocation";

    private static String debugLocation;

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                                 boolean loaded, DynamicType dynamicType) {
        if (debugLocation == null) {
            return;
        }

        String name = typeDescription.getName();
//        if (!Context.isTransformed(name)) {
//            return;
//        }

        dump(name, dynamicType.getBytes());
    }

    @Override
    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable ex) {
        LOGGER.log(Level.WARNING, "onError className: " + typeName + "message: " + ex.getMessage(), ex);
    }

    @Override
    public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
    }

    @Override
    public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        // LOGGER.info("onComplete "+typeName);
    }

    public static void dump(String name, byte[] bytes) {
        String dirs = name.replace('.', File.separatorChar);
        new File(debugLocation + File.separator + dirs).getParentFile().mkdirs();
        File file = new File(debugLocation + File.separator + dirs + ".class");

        System.err.println("dump class " + file.getPath());

        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
            stream.write(bytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static {
        debugLocation = System.getProperty(DEBUG_LOCATION_PROPERTY);
    }
}
