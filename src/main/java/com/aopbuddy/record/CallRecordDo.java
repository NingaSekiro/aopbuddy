package com.aopbuddy.record;

import com.aopbuddy.view.ObjectView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallRecordDo {
    private Long id;
    private Timestamp timestamp;

    private int callChainId;
    private String method;
    private String args;
    private String returnValue;
    private String target;
    private String threadName;
    private String message;
    private int depth;

    public static CallRecordDo toCallRecordDo(CallRecord callRecord) {
        CallRecordDo callRecordDo = new CallRecordDo();
        callRecordDo.setCallChainId(callRecord.getCallChainId());
        callRecordDo.setMethod(callRecord.getMethod());
        callRecordDo.setArgs(ObjectView.getDrawString(callRecord.getArgs(), 2));
        callRecordDo.setReturnValue(ObjectView.getDrawString(callRecord.getReturnValue(), 2));
        callRecordDo.setTarget(ObjectView.getDrawString(callRecord.getTarget(), 2));
        callRecordDo.setThreadName(callRecord.getThreadName());
        callRecordDo.setMessage(callRecord.getMessage());
        return callRecordDo;
    }

    public static boolean isInboundCall(CallRecordDo callRecordDo) {
        return callRecordDo.getArgs() != null && !callRecordDo.getArgs().equals("null");
    }
}
