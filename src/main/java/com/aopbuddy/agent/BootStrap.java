package com.aopbuddy.agent;

import cn.hutool.http.HttpUtil;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.servlet.ClassloaderServlet;
import com.aopbuddy.servlet.EvalServlet;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BootStrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class.getName());

    public static void start(String args, Instrumentation instrumentation) {
        try {
            Context.init(instrumentation);
//            instrumentation.appendToSystemClassLoaderSearch(new JarFile(file));
            HttpUtil.createServer(args != null ? Integer.parseInt(args) : 8888)
                    .addAction("/classloader", new ClassloaderServlet())
                    .addAction("/eval", new EvalServlet())
                    .start();
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE
                    , "BootStrap ERROR ", e);
            throw new RuntimeException(e);
        }
    }
}
