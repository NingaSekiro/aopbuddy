package com.aopbuddy.retransform;

import com.aopbuddy.agent.TraceListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.MethodChainUtil;
import com.aopbuddy.record.CaffeineCache;
import com.aopbuddy.record.MethodChain;
import com.aopbuddy.record.MethodChainKey;
import com.aopbuddytest.Model;
import com.aopbuddytest.TargetService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
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
        CaffeineCache.getCache().invalidateAll();
    }


    @Test
    public void testTrace() {
        Context.init(null);
        MethodPointcut pointcut = MethodPointcut.of(
                "com.aopbuddytest.*", "*", "(..)");
        Listener listener = new TraceListener();
        Context.registerAdvisor(pointcut, listener);


        TargetService svc = new TargetService();
        Model model = new Model();
        svc.greet(model);

        int size = CaffeineCache.getCache().asMap().size();
        MethodChainKey methodChainKey = new MethodChainKey();
        methodChainKey.setStartMethodName("public com.aopbuddytest.Model com.aopbuddytest.TargetService.greet(com.aopbuddytest.Model)");
        methodChainKey.getLineNums().add(18);
        MethodChain methodChain = CaffeineCache.get(methodChainKey);
        int size1 = methodChain.getCallRecordDos().size();
        Map<MethodChainKey, MethodChain> collect = MethodChainUtil.filterTime(CaffeineCache.getCache().asMap(), 0);
        assertEquals(1, size);
        assertEquals(0, CALL_CONTEXT.get().size());
        assertEquals(0, CALL_CHAIN_CONTEXT.get().getCallRecords().size());
    }

    @Test
    public void testParallel() throws InterruptedException, IOException {
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

        MethodChainKey methodChainKey = new MethodChainKey();
        methodChainKey.setStartMethodName("public com.aopbuddytest.Model com.aopbuddytest.TargetService.greet(com.aopbuddytest.Model)");
        methodChainKey.getLineNums().add(18);
        MethodChain methodChain = CaffeineCache.get(methodChainKey);
        int size1 = methodChain.getCallRecordDos().size();
        String json = JsonUtil.toJson(CaffeineCache.getCache().asMap());
        Map<MethodChainKey, MethodChain> map = JsonUtil.parse(json, new TypeReference<Map<MethodChainKey, MethodChain>>() {
        });
        assertEquals(8, size1);
        assertEquals(0, CALL_CONTEXT.get().size());
        assertEquals(0, CALL_CHAIN_CONTEXT.get().getCallRecords().size());
    }


//    @Test
//
//    public void testJson() throws InterruptedException {
//        Cache<String, MethodChain> cache = CaffeineCache.getCache();
//        CaffeineCache.put("pxx", new MethodChain());
//        ConcurrentMap<@NonNull String, @NonNull MethodChain> map = cache.asMap();
//        String jsonStr = JSON.toJSONString(map);
//        System.out.println(jsonStr);
//        Map<String, MethodChain> map1 = JSON.parseObject(jsonStr, new TypeReference<Map<String, MethodChain>>() {
//        }.getType());
//        System.out.println("d");
//    }
//
//    @Test
//    public void testJson2() throws InterruptedException {
//        MethodChain methodChain = new MethodChain();
//        Map<String, MethodChain> map = new HashMap<>();
//        map.put("pxx", methodChain);
//        String jsonStr = JSON.toJSONString(map);
//        System.out.println(jsonStr);
//        Map<String, MethodChain> o = JSON.parseObject(jsonStr, new TypeReference<Map<String, MethodChain>>() {
//        }.getType());
//        System.out.println(o);
//    }
}
