package com.aopbuddy.record;

import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CHAIN_CONTEXT;
import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CONTEXT;

import com.aopbuddy.bytekit.MethodInfo;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.infrastructure.StringUtils;
import com.aopbuddy.retransform.Listener;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class TraceListener implements Listener {

  private static final Logger LOGGER = LoggerFactory.getLogger(TraceListener.class.getName());

  @Override
  public void before(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args) {
    // 获取方法栈
    Stack<CallRecord> callRecords = CALL_CONTEXT.get();
    int threadLocalMethodId = CALL_CHAIN_CONTEXT.get().getCallRecords().size();
    CallRecord callRecord = CallRecord.builder()
        .threadLocalMethodId(threadLocalMethodId).target(target).method(
            StringUtils.toCompleteMethodName(methodInfo.getMethodAccess(), clazz.getName(),
                methodInfo.getMethodName())).args(args)
        .build();
    if (!callRecords.isEmpty()) {
      CallRecord topCallRecord = callRecords.peek();
      topCallRecord.getChildIds().add(threadLocalMethodId);
      callRecord.setPath(topCallRecord.getPath() + "|" + threadLocalMethodId);
    } else {
      callRecord.setPath(String.valueOf(threadLocalMethodId));
    }

    // 判断是否是web请求
    if (callRecords.isEmpty()) {
      String message = extractMessage(target, clazz, methodInfo, args);
      callRecord.setMessage(message);
    }
    callRecords.push(callRecord);
    // 调用链
    CALL_CHAIN_CONTEXT.get().getCallRecords().add(callRecord);
  }


  @Override
  public void after(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args,
      Object returnValue) {
    extracted(clazz, methodInfo, args, returnValue);
  }


  @Override
  public void onException(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args,
      Throwable throwable) {
    extracted(clazz, methodInfo, args, throwable);
  }


  private static void extracted(Class<?> clazz, MethodInfo methodInfo, Object[] args,
      Object returnValue) {
    Stack<CallRecord> callRecords = CALL_CONTEXT.get();
    CallRecord beforeCallRecord = callRecords.pop();
    beforeCallRecord.setReturnValue(returnValue);
    // 方法调用完成
    if (callRecords.isEmpty()) {
      List<CallRecord> callRecords1 = CALL_CHAIN_CONTEXT.get().getCallRecords();
//      if (callRecords.size() > 200) {
//        callRecords1 = removeCycle(callRecords1);
//      }
      MethodChainKey methodChainKey = MethodChainKey.buildMethodChainKey(callRecords1);
      // 当前调用链结束，保存调用链
      int andIncrement = ByteBuddyCallTracer.CHAIN_CNT.getAndIncrement();
      for (CallRecord record : callRecords1) {
        record.setCallChainId(andIncrement);
      }
      // 设置线程名
      callRecords1.get(0).setThreadName(Thread.currentThread().getName());
      CallChainDo callChainDo = new CallChainDo();
      callChainDo.setCallRecords(
          callRecords1.stream().map(CallRecordDo::toCallRecordDo).collect(Collectors.toList()));
      callChainDo.setTime(System.currentTimeMillis());
      CaffeineCache.getCache().asMap().computeIfAbsent(methodChainKey, k -> new MethodChain());
      CaffeineCache.get(methodChainKey).getCallRecordDos().offer(callChainDo);
      // 重置调用链上下文
      CALL_CONTEXT.remove();
      CALL_CHAIN_CONTEXT.remove();
    }
  }

  /**
   * 提取消息（根据规则）
   */
  private static String extractMessage(Object target, Class<?> clazz, MethodInfo methodInfo,
      Object[] args) {
    if (target == null || clazz == null || methodInfo == null) {
      return null;
    }

    // 获取目标类型名和方法名
    Set<String> typeNames = Arrays.stream(target.getClass().getGenericInterfaces())
        .map((Type::getTypeName))
        .collect(Collectors.toSet());

    // 查找匹配的规则
    MethodMessageRule rule = MethodMessageRuleManager.findMatchingRule(typeNames,
        methodInfo.getMethodName());
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

  private static List<CallRecord> removeCycle(List<CallRecord> callRecords) {
    if (callRecords == null || callRecords.isEmpty()) {
      return callRecords;
    }
    Map<String, Integer> methodCount = new HashMap<>();
    List<CallRecord> result = new ArrayList<>();
    for (CallRecord record : callRecords) {
      String method = record.getMethod();
      int count = methodCount.getOrDefault(method, 0);
      if (count < 10) {
        result.add(record);
        methodCount.put(method, count + 1);
      }
    }
    return result;
  }
}