package com.aopbuddy.record;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setChainHash(12345L);

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method1");
        key2.setChainHash(12345L);
        
        assertEquals(key1.hashCode(), key2.hashCode());
    }
    
    @Test
    void testNotEquals_DifferentChainHash() {
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setChainHash(12345L);

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method1");
        key2.setChainHash(67890L);
        
        assertNotEquals(key1, key2);
    }
    
    @Test
    void testNotEquals_DifferentStartMethod() {
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setChainHash(12345L);

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method2");
        key2.setChainHash(12345L);
        
        assertNotEquals(key1, key2);
    }
}