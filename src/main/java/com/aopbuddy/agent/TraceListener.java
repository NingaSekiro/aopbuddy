package com.aopbuddy.agent;

import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.record.*;
import com.aopbuddy.retransform.Advisor;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.retransform.Listener;
import javafx.util.Pair;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.aopbuddy.record.ByteBuddyCallTracer.*;


public class TraceListener implements Listener {
    @Override
    public void before(Object target, Method method, Object[] args) {
        // 获取调用上下文
        Stack<CallRecord> callRecords = CALL_CONTEXT.get();
        CallRecord callRecord = CallRecord.builder().target(target).method(method.toString()).args(args).build();
        if (callRecords.isEmpty()) {
            String message = extractMessage(target, method, args);
            callRecord.setMessage(message);
        }
        callRecords.push(callRecord);
        CALL_CHAIN_CONTEXT.get().getCallRecords().add(callRecord);
        BEFORE_METHOD_INDEX_MAP.get().computeIfAbsent(method.toString(), k -> new ArrayList<>()).add(CALL_CHAIN_CONTEXT.get().getCallRecords().size() - 1);

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
        RETURN_METHOD_INDEX_MAP.get().computeIfAbsent(method.toString(), k -> new ArrayList<>()).add(callRecords1.size() - 1);
        if (callRecords.isEmpty()) {
            List<Pair<Integer, Integer>> methodPairs = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> stringListEntry : RETURN_METHOD_INDEX_MAP.get().entrySet()) {
                if (stringListEntry.getValue().size() <= 1) {
                    continue;
                }
                List<Integer> afterIndex = stringListEntry.getValue();
                String key = stringListEntry.getKey();
                List<Integer> beforeIndex = BEFORE_METHOD_INDEX_MAP.get().get(key);
                for (int i = 0; i < afterIndex.size() - 1; i++) {
                    int preReturn = afterIndex.get(i);
                    if (preReturn + 1 == beforeIndex.get(i + 1)) {
                        methodPairs.add(new Pair<>(beforeIndex.get(i), afterIndex.get(i)));
                    }
                }
            }
            // 从后往前删除，避免索引偏移问题
            methodPairs.sort((a, b) -> b.getKey() - a.getKey()); // 按开始索引降序排序
            for (Pair<Integer, Integer> pair : methodPairs) {
                int start = pair.getKey();
                int end = pair.getValue();
                // 确保索引在有效范围内
                if (start >= 0 && end < callRecords1.size() && start <= end) {
                    // 从end到start删除，避免索引变化问题
                    callRecords1.subList(start, end + 1).clear();
                }
            }
            MethodChainKey methodChainKey = MethodChainKey.buildMethodChainKey(callRecords1);
            // 当前调用链结束，保存调用链
            int andIncrement = ByteBuddyCallTracer.CHAIN_CNT.getAndIncrement();
            for (CallRecord record : callRecords1) {
                record.setCallChainId(andIncrement);
            }
            // 设置线程名
            callRecords1.get(0).setThreadName(Thread.currentThread().getName());
            CallChainDo callChainDo = new CallChainDo();
            callChainDo.setCallRecords(callRecords1.stream().map(CallRecordDo::toCallRecordDo).collect(Collectors.toList()));
            callChainDo.setTime(System.currentTimeMillis());
            CaffeineCache.getCache().asMap().computeIfAbsent(methodChainKey, k -> new MethodChain());
            CaffeineCache.get(methodChainKey).getCallRecordDos().offer(callChainDo);
            // 重置调用链上下文
            CALL_CONTEXT.remove();
            CALL_CHAIN_CONTEXT.remove();
            BEFORE_METHOD_INDEX_MAP.remove();
            RETURN_METHOD_INDEX_MAP.remove();
        }
    }

    /**
     * 提取消息（根据规则）
     */
    private static String extractMessage(Object target, Method method, Object[] args) {
        if (target == null || method == null) {
            return null;
        }

        // 获取目标类型名和方法名
        Set<String> typeNames = Arrays.stream(target.getClass().getGenericInterfaces())
                .map((Type::getTypeName))
                .collect(Collectors.toSet());
        String methodName = method.getName();

        // 查找匹配的规则
        MethodMessageRule rule = MethodMessageRuleManager.findMatchingRule(typeNames, methodName);
        if (rule == null || rule.getMessageExpression() == null) {
            return null;
        }

        // 执行表达式
        try {
            return MessageExpressionEvaluator.evaluate(rule.getMessageExpression(), args);
        } catch (Exception e) {
            // 表达式执行失败，返回 null（可以记录日志）
            return null;
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