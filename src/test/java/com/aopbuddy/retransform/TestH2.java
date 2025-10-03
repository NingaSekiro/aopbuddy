package com.aopbuddy.retransform;

import cn.hutool.core.lang.Singleton;
import com.aopbuddy.agent.TraceListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.mapper.BaseDao;
import com.aopbuddy.mapper.CallRecordDo;
import com.aopbuddy.mapper.CallRecordMapper;
import com.aopbuddytest.Model;
import com.aopbuddytest.TargetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CHAIN_CONTEXT;
import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestH2 {

    @AfterEach
    public void cleanup() {
        Context.ADVISORS.clear();
        BaseDao baseDao = Singleton.get(BaseDao.class);
        baseDao.execute(CallRecordMapper.class, mapper -> {
            mapper.clearAllRecords();
            return null;
        });
    }


    @Test
    public void testTrace() throws InterruptedException {
        Context.init(null);
//        JSON.config(JSONWriter.Feature.FieldBased);
//        JSON.config(JSONReader.Feature.FieldBased);
//        JSON.config(JSONWriter.Feature.WriteClassName );
//        JSON.config(JSONReader.Feature.SupportClassForName);
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.*", "*", "(..)");
        Listener listener = new TraceListener();
        Context.registerAdvisor(pointcut, listener);


        TargetService svc = new TargetService();
        Model model = new Model();
        svc.greet(model);

        BaseDao baseDao = Singleton.get(BaseDao.class);
        List<CallRecordDo> execute = baseDao.execute(CallRecordMapper.class, mapper -> {
            return mapper.selectByIdGreaterThan(-1L);
        });
        assertEquals(4, execute.size());
        assertEquals(0, CALL_CONTEXT.get().size());
        assertEquals(0, CALL_CHAIN_CONTEXT.get().getCallRecords().size());
    }

    @Test
    public void testParallel() throws InterruptedException {
        Context.init(null);
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.*", "*", "(..)");
        Listener listener = new TraceListener();
        Context.registerAdvisor(pointcut, listener);

        ExecutorService executor = Executors.newFixedThreadPool(200);

        // 提交多个任务
        for (int i = 0; i < 200; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                TargetService svc = new TargetService();
                Model model = new Model();
                model.setSource("thread" + threadNum);
                svc.greet(model);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        BaseDao baseDao = Singleton.get(BaseDao.class);
        List<CallRecordDo> execute = baseDao.execute(CallRecordMapper.class, mapper -> {
            return mapper.selectByIdGreaterThan(-1L);
        });
        assertEquals(800, execute.size());
        assertEquals(0, CALL_CONTEXT.get().size());
        assertEquals(0, CALL_CHAIN_CONTEXT.get().getCallRecords().size());
    }
}
