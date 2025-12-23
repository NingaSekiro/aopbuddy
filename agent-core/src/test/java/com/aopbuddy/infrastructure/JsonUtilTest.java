package com.aopbuddy.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.alibaba.fastjson2.JSON;
import com.aopbuddy.record.TraceListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Advisor;
import org.junit.jupiter.api.Test;


public class JsonUtilTest {

  @Test
  public void testSerializeAdvisorWithTraceListener() {
    // Create a TraceListener
    TraceListener listener = new TraceListener();

    // Create a MethodPointcut
    MethodPointcut pointcut = MethodPointcut.of("*", "*", "*");

    // Create an Advisor with the TraceListener
    Advisor advisor = new Advisor(pointcut, listener);

    // Serialize the advisor - this should not throw an exception anymore
    String json = JsonUtil.toJson(advisor);

    // Verify that serialization worked
    assertNotNull(json);
    assertTrue(json.length() > 0);
    
    // Verify that the listener is serialized with its class name as key
    assertFalse(json.contains("listener"));
    assertTrue(json.contains("TraceListener"));

    System.out.println("Serialized Advisor: " + json);
  }


  @Test
  public void testSerializeString() {
    String json = JsonUtil.toJson("{\"code\":1,\"msg\":null,\"data\":\"删除成功\",\"map\":{}}");
    assertNotNull(json);
    assertTrue(json.length() > 0);
    System.out.println("Serialized String: " + json);
  }

  @Test
  public void testSerializeString_fast_json() {
    String json = JSON.toJSONString(
        "{\"code\":1,\"msg\":null,\"data\":\"删除成功\",\"map\":{}}");
    assertNotNull(json);
    assertTrue(json.length() > 0);
    System.out.println("Serialized String: " + json);
  }


}