package com.aopbuddy.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用记录，存储单个方法调用的详细信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 方法入参，target，出参
public class CallRecord {
    private int callChainId;
    private String method;
    private Object[] args;
    private Object returnValue;
    private Object target;
    private Throwable exception;
    private String threadName;
    private String message;
    private int depth;
}