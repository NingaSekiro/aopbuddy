package com.aopbuddy.groovy;

import java.net.URL;
import java.net.URLClassLoader;

public class GroovyConsoleLoader extends URLClassLoader {
    private static final URL[] EMPTY_URL_ARRAY = new URL[0];
    private final ClassLoader assistantLoader;
    private final String[] groovyPackages;

    public GroovyConsoleLoader(ClassLoader parent) {
        super(EMPTY_URL_ARRAY, GroovyConsoleLoader.class.getClassLoader());
        this.assistantLoader = parent;
        this.groovyPackages = new String[]{"groovy.", "groovyjarjarantlr.", "groovyjarjarasm.asm.", "groovyjarjarcommonscli.", "org.apache.groovy.", "org.codehaus.groovy."};
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException var4) {
            return this.assistantLoader.loadClass(name);
        }
    }
}