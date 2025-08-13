package com.aopbuddy.aspect;


import aj.org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface MethodObject {
    String getClassName();

    String getName();

    String getDescriptor();


    List<String> getAnnotations();

    class ForMethod implements MethodObject {
        private Method method;

        public ForMethod(Method method) {
            this.method = method;
        }

        @Override
        public String getClassName() {
            return method.getDeclaringClass().getName();
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public String getDescriptor() {
            return Type.getMethodDescriptor(method);
        }

        @Override
        public List<String> getAnnotations() {
            return Arrays.stream(method.getAnnotations()).map(Annotation::annotationType)
                    .map(Class::getName).collect(Collectors.toList());
        }
    }

    class ForString implements MethodObject {
        private String className;
        private String name;
        private String descriptor;

        public ForString(String className, String name, String descriptor) {
            this.className = className;
            this.name = name;
            this.descriptor = descriptor;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getClassName() {
            return className;
        }

        @Override
        public String getDescriptor() {
            return descriptor;
        }

        @Override
        public List<String> getAnnotations() {
            return null;
        }
    }

}
