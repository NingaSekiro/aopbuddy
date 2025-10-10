package com.aopbuddy.record;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/*
* 调用链
*/
@Data
public class CallChainDo {
    private List<CallRecordDo> callRecords = new ArrayList<>();
    private long time;
}
