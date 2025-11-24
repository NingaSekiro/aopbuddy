package com.aopbuddy.infrastructure;

import com.aopbuddy.record.TraceListener;
import com.aopbuddy.aspect.MethodPointcut;
import com.aopbuddy.retransform.Advisor;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        System.out.println("Serialized Advisor: " + json);
    }
}