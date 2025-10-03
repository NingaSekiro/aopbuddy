package com.aopbuddy.mapper;

import com.aopbuddy.record.CallRecord;
import com.aopbuddy.view.ObjectVO;
import com.aopbuddy.view.ObjectView;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class CallRecordDo {
    private Long id;
    private int callId;
    private String method;
    private String objectView;
    private Timestamp timestamp;

    public static CallRecordDo toCallRecordDo(CallRecord callRecord) {
        CallRecordDo callRecordDo = new CallRecordDo();
        callRecordDo.setCallId(callRecord.getCallChainId());
        callRecordDo.setMethod(callRecord.getMethod());
        List<Object> objectView = new ArrayList<>(Arrays.asList(callRecord.getArgs(), callRecord.getReturnValue(), callRecord.getTarget()));
        ObjectVO objectVO = new ObjectVO(objectView, 2);
        String draw = new ObjectView(Integer.MAX_VALUE, objectVO).draw();
        callRecordDo.setObjectView(draw);
        return callRecordDo;
    }
}
