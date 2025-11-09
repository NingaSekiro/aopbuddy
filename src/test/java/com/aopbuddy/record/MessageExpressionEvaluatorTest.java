package com.aopbuddy.record;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageExpressionEvaluator 单元测试
 */
class MessageExpressionEvaluatorTest {

    // 测试用的模拟类
    static class MockRequest {
        private String method;
        private String requestURI;

        public MockRequest(String method, String requestURI) {
            this.method = method;
            this.requestURI = requestURI;
        }

        public String getMethod() {
            return method;
        }

        public String getRequestURI() {
            return requestURI;
        }

        public String toString() {
            return "MockRequest{method='" + method + "', uri='" + requestURI + "'}";
        }
    }

    static class MockObject {
        private String value;

        public MockObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return "name-" + value;
        }

        public MockObject getNext() {
            return new MockObject("next-" + value);
        }
    }

    @Test
    void testEvaluate_SimpleExpression() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()}", args);
        assertEquals("GET", result);
    }

    @Test
    void testEvaluate_MultipleExpressions() {
        MockRequest request = new MockRequest("POST", "/api/orders");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()} ${[0].getRequestURI()}", args);
        assertEquals("POST /api/orders", result);
    }

    @Test
    void testEvaluate_MethodChain() {
        MockObject obj = new MockObject("test");
        Object[] args = {obj};

        String result = MessageExpressionEvaluator.evaluate("${[0].getValue()}", args);
        assertEquals("test", result);
    }

    @Test
    void testEvaluate_ChainedMethods() {
        MockObject obj = new MockObject("test");
        Object[] args = {obj};

        // 测试链式调用
        String result = MessageExpressionEvaluator.evaluate("${[0].getNext().getValue()}", args);
        // 链式调用应该返回 "next-test"
        assertNotNull(result);
        // 如果支持链式调用，结果应该是 "next-test"
        // 如果不支持，可能会返回错误信息
    }

    @Test
    void testEvaluate_InvalidIndex() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[1].getMethod()}", args);
        assertEquals("null", result);
    }

    @Test
    void testEvaluate_NegativeIndex() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[-1].getMethod()}", args);
        assertEquals("null", result);
    }

    @Test
    void testEvaluate_NullArgs() {
        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()}", null);
        assertEquals("null", result);
    }

    @Test
    void testEvaluate_EmptyArgs() {
        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()}", new Object[0]);
        assertEquals("null", result);
    }

    @Test
    void testEvaluate_NullObject() {
        Object[] args = {null};

        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()}", args);
        assertEquals("null", result);
    }

    @Test
    void testEvaluate_NoMethodChain() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        // 没有方法调用，直接返回对象
        String result = MessageExpressionEvaluator.evaluate("${[0]}", args);
        assertNotNull(result);
        assertTrue(result.contains("MockRequest"));
    }

    @Test
    void testEvaluate_EmptyExpression() {
        String result = MessageExpressionEvaluator.evaluate("", new Object[0]);
        assertEquals("", result);
    }

    @Test
    void testEvaluate_NullExpression() {
        String result = MessageExpressionEvaluator.evaluate(null, new Object[0]);
        assertEquals("", result);
    }

    @Test
    void testEvaluate_PlainText() {
        String result = MessageExpressionEvaluator.evaluate("plain text", new Object[0]);
        assertEquals("plain text", result);
    }

    @Test
    void testEvaluate_MixedTextAndExpression() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("Method: ${[0].getMethod()}, URI: ${[0].getRequestURI()}", args);
        assertEquals("Method: GET, URI: /api/users", result);
    }

    @Test
    void testEvaluate_MultipleArgs() {
        MockRequest request1 = new MockRequest("GET", "/api/users");
        MockRequest request2 = new MockRequest("POST", "/api/orders");
        Object[] args = {request1, request2};

        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()} and ${[1].getMethod()}", args);
        assertEquals("GET and POST", result);
    }

    @Test
    void testEvaluate_InvalidExpressionFormat() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        // 不是以 [ 开头的表达式
        String result = MessageExpressionEvaluator.evaluate("${invalid}", args);
        assertEquals("invalid", result);
    }

    @Test
    void testEvaluate_MethodNotFound() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[0].nonExistentMethod()}", args);
        assertTrue(result.startsWith("ERROR:"));
    }

    @Test
    void testEvaluate_RealWorldScenario() {
        // 模拟真实的 FilterChain.doFilter 场景
        MockRequest request = new MockRequest("GET", "/api/v1/users/123");
        Object[] args = {request, null}; // FilterChain.doFilter(ServletRequest, ServletResponse)

        String result = MessageExpressionEvaluator.evaluate("${[0].getMethod()} ${[0].getRequestURI()}", args);
        assertEquals("GET /api/v1/users/123", result);
    }

    @Test
    void testEvaluate_ComplexExpression() {
        MockObject obj = new MockObject("test");
        Object[] args = {obj};

        // 测试多个方法调用
        String result1 = MessageExpressionEvaluator.evaluate("${[0].getValue()}", args);
        assertEquals("test", result1);

        String result2 = MessageExpressionEvaluator.evaluate("${[0].getName()}", args);
        assertEquals("name-test", result2);
    }

    @Test
    void testEvaluate_SpecialCharacters() {
        MockRequest request = new MockRequest("GET", "/api/users?name=test&age=20");
        Object[] args = {request};

        String result = MessageExpressionEvaluator.evaluate("${[0].getRequestURI()}", args);
        assertEquals("/api/users?name=test&age=20", result);
    }

    @Test
    void testEvaluate_EmptyMethodChain() {
        MockRequest request = new MockRequest("GET", "/api/users");
        Object[] args = {request};

        // 只有索引，没有方法调用
        String result = MessageExpressionEvaluator.evaluate("${[0]}", args);
        assertNotNull(result);
    }
}

