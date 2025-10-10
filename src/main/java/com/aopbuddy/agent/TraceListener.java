package com.aopbuddy.agent;

import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.record.*;
import com.aopbuddy.retransform.Advisor;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.retransform.Listener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CHAIN_CONTEXT;
import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CONTEXT;


public class TraceListener implements Listener {
    @Override
    public void before(Object target, Method method, Object[] args) {
        // 获取调用上下文
        Stack<CallRecord> callRecords = CALL_CONTEXT.get();
        CallRecord callRecord = CallRecord.builder().target(target).method(method.toString()).args(args).build();
        callRecords.push(callRecord);
        CALL_CHAIN_CONTEXT.get().getCallRecords().add(callRecord);
    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        extracted(method, returnValue);
        return new MockedReturnValue(false, null);
    }


    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        extracted(method, throwable);
    }

    private static void extracted(Method method, Object returnValue) {
        Stack<CallRecord> callRecords = CALL_CONTEXT.get();
        callRecords.pop();
        // CALL_CONTEXT,设置返回值
        CallRecord callRecord = CallRecord.builder().method(method.toString()).returnValue(returnValue).build();
        List<CallRecord> callRecords1 = CALL_CHAIN_CONTEXT.get().getCallRecords();
        callRecords1.add(callRecord);
        if (callRecords.isEmpty()) {
            MethodPointcut methodPointcut = getMethodPointcut();
            MethodChainKey methodChainKey = new MethodChainKey();
            methodChainKey.setStartMethodName(method.toString());
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                if (methodPointcut.matchesClassName(stackTraceElement.getClassName())) {
                    methodChainKey.getLineNums().add(stackTraceElement.getLineNumber());
                }
            }
            // 当前调用链结束，保存调用链
            int andIncrement = ByteBuddyCallTracer.CHAIN_CNT.getAndIncrement();
            for (CallRecord record : callRecords1) {
                record.setCallChainId(andIncrement);
            }
            CallChainDo callChainDo = new CallChainDo();
            callChainDo.setCallRecords(callRecords1.stream().map(CallRecordDo::toCallRecordDo).collect(Collectors.toList()));
            callChainDo.setTime(System.currentTimeMillis());
            CaffeineCache.getCache().asMap().computeIfAbsent(methodChainKey, k -> new MethodChain());
            CaffeineCache.get(methodChainKey).getCallRecordDos().offer(callChainDo);
            // 重置调用链上下文
            CALL_CONTEXT.remove();
            CALL_CHAIN_CONTEXT.remove();
        }
    }

    private static MethodPointcut getMethodPointcut() {
        List<Advisor> advisors = Context.ADVISORS;
        for (Advisor advisor : advisors) {
            if (advisor.getListener() instanceof TraceListener) {
                return (MethodPointcut) advisor.getPointcut();
            }
        }
        return null;
    }
}