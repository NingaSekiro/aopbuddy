package com.aopbuddy.record;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CallChain {
    private List<CallRecord> callRecords = new ArrayList<>();
}
