package com.aopbuddy.record;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;

/*
* 某方法的调用链集合
*/
@Data
public class MethodChain {
    private ArrayBlockingQueue<CallChainDo> callRecordDos = new ArrayBlockingQueue<>(8);
}
