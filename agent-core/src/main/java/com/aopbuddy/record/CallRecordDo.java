package com.aopbuddy.record;

import com.aopbuddy.view.ObjectView;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallRecordDo {

  private Long id;
  private int threadLocalMethodId;
  private Timestamp timestamp;

  private int callChainId;
  private String method;
  private String args;
  private String returnValue;
  private String target;
  private String threadName;
  private String message;

  private List<Integer> childIds = new ArrayList<>();
  private String path;
  private static final String TEST = "cccc";

  public static CallRecordDo toCallRecordDo(CallRecord callRecord) {
    CallRecordDo callRecordDo = new CallRecordDo();
    callRecordDo.setCallChainId(callRecord.getCallChainId());
    callRecordDo.setMethod(callRecord.getMethod());
    callRecordDo.setArgs(ObjectView.getDrawString(callRecord.getArgs(), 2));
    callRecordDo.setReturnValue(ObjectView.getDrawString(callRecord.getReturnValue(), 2));
    callRecordDo.setTarget(ObjectView.getDrawString(callRecord.getTarget(), 2));
    callRecordDo.setThreadName(callRecord.getThreadName());
    callRecordDo.setMessage(callRecord.getMessage());
    callRecordDo.setThreadLocalMethodId(callRecord.getThreadLocalMethodId());
    callRecordDo.setPath(callRecord.getPath());
    callRecordDo.setChildIds(callRecord.getChildIds());
    return callRecordDo;
  }

  public static boolean isInboundCall(CallRecordDo callRecordDo) {
    return callRecordDo.getArgs() != null && !callRecordDo.getArgs().equals("null");
  }
}
