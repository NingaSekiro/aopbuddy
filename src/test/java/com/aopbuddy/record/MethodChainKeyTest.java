package com.aopbuddy.record;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class MethodChainKeyTest {

    @Test
    void testEquals() {
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setLineNums(Arrays.asList(1, 2, 3));

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method1");
        key2.setLineNums(Arrays.asList(1, 2, 3));
        assert key1.equals(key2);
    }

    @Test
    void testHashCode() {
        MethodChainKey key1 = new MethodChainKey();
        key1.setStartMethodName("method1");
        key1.setLineNums(Arrays.asList(1, 2, 3));

        MethodChainKey key2 = new MethodChainKey();
        key2.setStartMethodName("method1");
        key2.setLineNums(Arrays.asList(1, 2, 3));
        assert key1.hashCode() == key2.hashCode();
    }
}