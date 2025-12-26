package com.aopbuddy.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.record.CallChainDo;
import com.aopbuddy.record.TraceListener;
import com.aopbuddy.retransform.Advisor;
import java.util.concurrent.ArrayBlockingQueue;
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
  public void test_jackson_ArrayBlockingQueue(){
    ArrayBlockingQueue<CallChainDo> callRecordDos = new ArrayBlockingQueue<>(10);
    CallChainDo callChainDo = new CallChainDo();
    callChainDo.setTime(System.currentTimeMillis());
    callRecordDos.add(callChainDo);
    String jsonString = JsonUtil.toJson(callRecordDos);
    JsonUtil.parse(jsonString, new com.fasterxml.jackson.core.type.TypeReference<ArrayBlockingQueue<CallChainDo>>(){});
    System.out.println(jsonString);
  }
}