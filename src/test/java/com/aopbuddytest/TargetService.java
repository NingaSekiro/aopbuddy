package com.aopbuddytest;


import lombok.Setter;

@Setter
public class TargetService {

    public String name;

    public String greetString(String name) {
        System.out.println("Service.greet running");
        for (int i = 0; i < 10; i++) {
            greet(i);
        }
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

    public void cycleStart(){
        for (int i = 0; i < 10; i++) {
            cycle();
        }
    }

    public String cycle(){
        cycle2();
        cycle3();
        return "dd";
    }
    public void cycle2(){
        cycle4();
    }
    public void cycle3(){

    }

    public void cycle4(){

    }

}