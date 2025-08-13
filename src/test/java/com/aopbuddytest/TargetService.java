package com.aopbuddytest;

public  class TargetService {
        public String greet(String name) {
            System.out.println("Service.greet running");
            return "Hello " + name;
        }

        public String greet(Integer n) {
            System.out.println("Service.greet(Integer) running");
            return "Num " + n;
        }
    }