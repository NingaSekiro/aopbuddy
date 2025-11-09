package com.aopbuddy.record;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.aopbuddy.record.MethodChainKey.buildMethodChainKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodChainKeyTest {

    @Test
    void testEquals() {
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setChainHash(12345L);

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method1");
        key2.setChainHash(12345L);

        assertEquals(key1, key2);
        assertTrue(key1.equals(key2));
    }

    @Test
    void testHashCode() {
        List<CallRecord> callRecords = new ArrayList<>();
        CallRecord callRecord1 = new CallRecord();
        callRecord1.setArgs(new Object[]{"arg1"});
        callRecord1.setMethod("public void com.fubukiss.rikky.filter.LoginCheckFilter.doFilter(javax.servlet.ServletRequest,javax.servlet.ServletResponse,javax.servlet.FilterChain) throws java.io.IOException,javax.servlet.ServletException");
        callRecords.add(callRecord1);
        MethodChainKey key = buildMethodChainKey(callRecords);
        MethodChainKey key2 = buildMethodChainKey(callRecords);
        assertEquals(key, key2);
        assertTrue(key.equals(key2));
    }
}