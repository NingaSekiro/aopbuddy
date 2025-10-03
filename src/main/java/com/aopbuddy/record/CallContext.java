package com.aopbuddy.record;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用上下文，维护线程的调用栈
 */
@Data
public class CallContext {
    // 调用栈深度
    private int depth = 0;
    // 当前调用ID
    private String currentCallId = null;
    // 根调用ID，用于标识完整调用链
    private String rootCallId = null;
    private List<String> callChain = new ArrayList<>();

    // 增加深度并返回新的调用ID
    public void enterMethod(Method method) {
        callChain.add("enter"+method.getName());
        this.depth++;
    }

    // 减少深度并返回父调用ID
    public void exitMethod(Object returnValue) {
        this.depth--;
        callChain.add("exit"+returnValue.toString());
    }

}