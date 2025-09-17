package com.aopbuddy.aspect;


import java.util.List;

public interface MethodObject {
    String getClassName();

    String getName();

    String getDescriptor();


    List<String> getAnnotations();


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
