package com.aopbuddy.record;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;


public class ByteBuddyCallTracer {
    // 方法栈本地存储，一次方法链调用完后会清空
    public static final ThreadLocal<Stack<CallRecord>> CALL_CONTEXT = ThreadLocal.withInitial(Stack::new);

    // 调用链上下文，用于记录方法调用链，一次方法链调用完后会清空
    public static  ThreadLocal<CallChain> CALL_CHAIN_CONTEXT = ThreadLocal.withInitial(CallChain::new);

    public static AtomicInteger count = new AtomicInteger(0);
}