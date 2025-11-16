package com.aopbuddy.retransform;

import com.aopbuddy.aspect.Pointcut;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Advisor {
    private final Pointcut pointcut;
    private final Listener listener;
    // 处理过的方法级别key
    protected Set<String> signatures = new LinkedHashSet<>();


    public Advisor(Pointcut pointcut, Listener listener) {
        this.pointcut = pointcut;
        this.listener = listener;
    }

    public synchronized void addSignature(String signature) {
        boolean added = signatures.add(signature);
        if (added) {
            Context.addCache(signature, listener);
        }
    }

    public synchronized void removeSignature() {
        Context.deleteCache(signatures, listener);
    }
}
