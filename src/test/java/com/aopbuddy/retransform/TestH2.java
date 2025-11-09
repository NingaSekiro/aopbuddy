package com.aopbuddy.retransform;

import com.aopbuddy.agent.TraceListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.infrastructure.JsonUtil;
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
import static org.junit.jupiter.api.Assertions.*;

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
        // 由于 MethodChainKey 现在由系统自动构造，需要从缓存中获取实际的 key
        Map<MethodChainKey, MethodChain> cacheMap = CaffeineCache.getCache().asMap();
        assertEquals(1, size);
        // 验证缓存中有数据
        assertEquals(1, cacheMap.size());
        // 获取第一个 key（应该是自动生成的）
        MethodChainKey methodChainKey = cacheMap.keySet().iterator().next();
        // 验证入口方法名正确
        assertEquals("public com.aopbuddytest.Model com.aopbuddytest.TargetService.greet(com.aopbuddytest.Model)", 
                     methodChainKey.getStartMethodName());
        MethodChain methodChain = CaffeineCache.get(methodChainKey);
        assertNotNull(methodChain);
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

        // 由于 MethodChainKey 现在由系统自动构造，需要从缓存中获取实际的 key
        Map<MethodChainKey, MethodChain> cacheMap = CaffeineCache.getCache().asMap();
        // 获取第一个 key（应该是自动生成的）
        MethodChainKey methodChainKey = cacheMap.keySet().iterator().next();
        // 验证入口方法名正确
        assertEquals("public com.aopbuddytest.Model com.aopbuddytest.TargetService.greet(com.aopbuddytest.Model)", 
                     methodChainKey.getStartMethodName());
        MethodChain methodChain = CaffeineCache.get(methodChainKey);
        assertNotNull(methodChain);
        int size1 = methodChain.getCallRecordDos().size();
        assertEquals(8, size1);
        // 测试 JSON 序列化和反序列化
        String json = JsonUtil.toJson(CaffeineCache.getCache().asMap());
        Map<MethodChainKey, MethodChain> map = JsonUtil.parse(json, new TypeReference<Map<MethodChainKey, MethodChain>>() {
        });
        assertNotNull(map);
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
