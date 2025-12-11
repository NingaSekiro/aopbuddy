package com.aopbuddy.aspect;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void testToClassName() {
        List<String> listItems =new ArrayList<>();
        listItems.add("com.example.Test");
        listItems.add("com.other.Test");
        String className = toClassName(listItems);
        parser = new PointcutParser(className, "*", "(..)");
        assertTrue(parser.isClass("com.example.Test.ddd.ccc"));

    }

    private  String toClassName(List<String> listItems) {
        StringBuilder regexBuilder = new StringBuilder("^(");
        for (int i = 0; i < listItems.size(); i++) {
            regexBuilder.append(listItems.get(i)+"..*");
            if (i < listItems.size() - 1) {
                regexBuilder.append("|");
            }
        }
        regexBuilder.append(")$");
        return regexBuilder.toString();
    }
}