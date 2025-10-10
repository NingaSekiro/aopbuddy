package com.aopbuddy.aspect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointcutParserTest {

    private PointcutParser parser;
    /**
     * 测试 isClass 方法是否能正确匹配类名
     */
    @Test
    void testIsClass() {
        parser = new PointcutParser("com.example.*", "test*", "(Ljava/lang/String;)V");
        assertTrue(parser.isClass("com.example.Test"));
        assertFalse(parser.isClass("com.other.Test"));
    }

    @Test
    void testIsClass2() {
        parser = new PointcutParser("com.example..*", "test*", "(Ljava/lang/String;)V");
        assertTrue(parser.isClass("com.example.d.d.d.Test"));
    }
}