//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.aopbuddy.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class AopAgentLoader extends URLClassLoader {
    public AopAgentLoader(URL[] urls) {
        super(attachToolsJar(urls), AopAgentLoader.class.getClassLoader().getParent());
    }

    protected static URL[] attachToolsJar(URL[] urls) {
        try {
            getSystemClassLoader().loadClass("com.sun.tools.attach.VirtualMachine");
            return urls;
        } catch (ClassNotFoundException var4) {
            File toolsFile = findToolsByCurrentJavaHome();
            if (toolsFile == null) {
                toolsFile = findToolsByCLASSPATH();
            }

            if (toolsFile == null) {
                toolsFile = findToolsByJAVAHOME();
            }

            if (toolsFile == null) {
                throw new IllegalStateException("系统找不到tools.jar");
            } else {
                try {
                    urls = (URL[])Arrays.copyOf(urls, urls.length + 1);
                    urls[urls.length - 1] = toolsFile.toURI().toURL();
                    return urls;
                } catch (MalformedURLException var3) {
                    MalformedURLException e = var3;
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static File findToolsByCurrentJavaHome() {
        if (!System.getProperty("java.home").endsWith("jre")) {
            return null;
        } else {
            File file = new File((new File(System.getProperty("java.home"))).getParent(), "lib/tools.jar");
            return file.exists() ? file : null;
        }
    }

    private static File findToolsByCLASSPATH() {
        String classpath = System.getenv("CLASSPATH");
        return classpath == null ? null : (File)Arrays.stream(classpath.split(System.getProperty("path.separator"))).filter((s) -> {
            return s.endsWith("tools.jar");
        }).map(File::new).filter(File::exists).findFirst().orElse(null);
    }

    private static File findToolsByJAVAHOME() {
        String JAVA_HOME = System.getenv("JAVA_HOME");
        if (JAVA_HOME == null) {
            return null;
        } else {
            File file = new File(JAVA_HOME, "lib/tools.jar");
            return file.exists() ? file : null;
        }
    }

    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = this.findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        } else if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        } else {
            try {
                Class<?> aClass = this.findClass(name);
                if (resolve) {
                    this.resolveClass(aClass);
                }

                return aClass;
            } catch (Exception var5) {
                return super.loadClass(name, resolve);
            }
        }
    }
}
