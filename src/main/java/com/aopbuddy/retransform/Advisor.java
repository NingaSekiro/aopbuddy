package com.aopbuddy.retransform;

import com.aopbuddy.aspect.Pointcut;
import lombok.Data;

@Data
public class Advisor {
    private final Pointcut pointcut;
    private final Listener listener;

    public Advisor(Pointcut pointcut, Listener listener) {
        this.pointcut = pointcut;
        this.listener = listener;
    }
}
