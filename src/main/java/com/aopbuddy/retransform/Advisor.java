package com.aopbuddy.retransform;

import lombok.Data;

import java.util.List;

@Data
public class Advisor {
    private final Pointcut pointcut;
    private final Listener listener;

    public Advisor(Pointcut pointcut, Listener listener) {
        this.pointcut = pointcut;
        this.listener = listener;
    }
}
