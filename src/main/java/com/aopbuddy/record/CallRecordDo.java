package com.aopbuddy.record;

import com.aopbuddy.view.ObjectView;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CallRecordDo {
    private Long id;
    private int callChainId;
    private String method;
    private String args;
    private String returnValue;
    private String target;
    private Timestamp timestamp;

    public static CallRecordDo toCallRecordDo(CallRecord callRecord) {
        CallRecordDo callRecordDo = new CallRecordDo();
        callRecordDo.setCallChainId(callRecord.getCallChainId());
        callRecordDo.setMethod(callRecord.getMethod());
        callRecordDo.setArgs(ObjectView.getDrawString(callRecord.getArgs(), 2));
        callRecordDo.setReturnValue(ObjectView.getDrawString(callRecord.getReturnValue(), 2));
        callRecordDo.setTarget(ObjectView.getDrawString(callRecord.getTarget(), 2));
        return callRecordDo;
    }
}
