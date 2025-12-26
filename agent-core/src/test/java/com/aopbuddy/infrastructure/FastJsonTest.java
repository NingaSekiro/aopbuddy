package com.aopbuddy.infrastructure;

import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CHAIN_CONTEXT;
import static com.aopbuddy.record.ByteBuddyCallTracer.CALL_CONTEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.record.CaffeineCache;
import com.aopbuddy.record.CallChainDo;
import com.aopbuddy.record.MethodChain;
import com.aopbuddy.record.MethodChainKey;
import com.aopbuddy.record.TraceListener;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.retransform.Listener;
import com.aopbuddytest.TargetService;
import com.aopbuddytest.TestHelper;
import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import lombok.SneakyThrows;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FastJsonTest {
  @SneakyThrows
  @BeforeEach
  public void init(){
    Instrumentation inst = ByteBuddyAgent.install();
    TestHelper.appendSpyJar( inst);
    Context.init(inst);
  }

  @AfterEach
  public void cleanup() {
    Context.ADVISORS.clear();
    CaffeineCache.getCache().invalidateAll();
  }
  @Test
  public void testTrace() {
    MethodPointcut pointcut = MethodPointcut.of(
        "com.aopbuddytest.TargetService", "*", "(..)");
    Listener listener = new TraceListener();
    Context.registerAdvisor(pointcut, listener);

    TargetService svc = new TargetService();
    String again = svc.greetString("again");


    // 由于 MethodChainKey 现在由系统自动构造，需要从缓存中获取实际的 key
    Map<MethodChainKey, MethodChain> cacheMap = CaffeineCache.getCache().asMap();
    // 验证缓存中有数据
    assertEquals(1, cacheMap.size());
    // 获取第一个 key（应该是自动生成的）
    MethodChainKey methodChainKey = cacheMap.keySet().iterator().next();
    // 验证入口方法名正确
    assertEquals("1|com.aopbuddytest.TargetService|greetString",
        methodChainKey.getStartMethodName());
    MethodChain methodChain = CaffeineCache.get(methodChainKey);
    String json = JSON.toJSONString(cacheMap);
    Map<MethodChainKey, MethodChain> map = JSON.parseObject(json, new TypeReference<Map<MethodChainKey, MethodChain>>() {
    });
    assertNotNull(methodChain);
    assertEquals(0, CALL_CONTEXT.get().size());
    assertEquals(0, CALL_CHAIN_CONTEXT.get().getCallRecords().size());
  }

  @Test
  public void test_fast_json_ArrayBlockingQueue() {
    ArrayBlockingQueue<CallChainDo> callRecordDos = new ArrayBlockingQueue<>(10);
    CallChainDo callChainDo = new CallChainDo();
    callChainDo.setTime(System.currentTimeMillis());
    callRecordDos.add(callChainDo);
    String jsonString = JSON.toJSONString(callRecordDos);
    JSON.parseArray(jsonString, CallChainDo.class);
    System.out.println(jsonString);
  }
}
