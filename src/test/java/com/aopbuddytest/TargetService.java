package com.aopbuddytest;


import lombok.Setter;

@Setter
public class TargetService {

    public String name;

    public String greetString(String name) {
        System.out.println("Service.greet running");
        return greet(1);
    }

    public String greet(Integer n) {
        System.out.println("Service.greet(Integer) running");
        return "Num " + n;
    }

    public Model greet(Model model) {
        System.out.println("Service.greet running");
        model.setSource(greet(1));
        return model;
    }
}