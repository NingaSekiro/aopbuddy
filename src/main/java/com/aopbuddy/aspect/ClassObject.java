package com.aopbuddy.aspect;

public interface ClassObject {
    String getName();

    class ForClass implements ClassObject {
        private final Class clz;

        public ForClass(Class clz) {
            this.clz = clz;
        }

        @Override
        public String getName() {
            return clz.getName();
        }
    }

    class ForUnloaded implements ClassObject {
        private final String name;

        public ForUnloaded(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
