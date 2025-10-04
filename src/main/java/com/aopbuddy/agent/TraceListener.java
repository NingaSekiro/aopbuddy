package com.aopbuddy.agent;

import cn.hutool.core.lang.Singleton;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.mapper.BaseDao;
import com.aopbuddy.mapper.CallRecordDo;
import com.aopbuddy.mapper.CallRecordMapper;
import com.aopbuddy.record.ByteBuddyCallTracer;
import com.aopbuddy.record.CallRecord;
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
        Stack<CallRecord> callRecords = CALL_CONTEXT.get();
        callRecords.pop();
        // CALL_CONTEXT,设置返回值
        CallRecord callRecord = CallRecord.builder().method(method.toString()).returnValue(returnValue).build();
        List<CallRecord> callRecords1 = CALL_CHAIN_CONTEXT.get().getCallRecords();
        callRecords1.add(callRecord);
        if (callRecords.isEmpty()) {
            // 当前调用链结束，保存调用链
            int andIncrement = ByteBuddyCallTracer.count.getAndIncrement();
            for (CallRecord record : callRecords1) {
                record.setCallChainId(andIncrement);
            }
            List<CallRecordDo> collect = callRecords1.stream().map(CallRecordDo::toCallRecordDo).collect(Collectors.toList());
            BaseDao baseDao = Singleton.get(BaseDao.class);
            baseDao.execute(CallRecordMapper.class, mapper -> {
                mapper.insertBatchCallRecords(collect);
                return null;
            });
            // 重置调用链上下文
            CALL_CONTEXT.remove();
            CALL_CHAIN_CONTEXT.remove();
        }
        return new MockedReturnValue(false, null);
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        // 异常处理逻辑
    }

}