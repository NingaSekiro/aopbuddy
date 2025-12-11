package com.aopbuddy.infrastructure;

import com.aopbuddy.record.CallChainDo;
import com.aopbuddy.record.MethodChain;
import com.aopbuddy.record.MethodChainKey;

import java.util.HashMap;
import java.util.Map;

public class MethodChainUtil {
    public static Map<MethodChainKey, MethodChain> filterTime(Map<MethodChainKey, MethodChain> map, long time) {
        Map<MethodChainKey, MethodChain> result = new HashMap<>();
        for (Map.Entry<MethodChainKey, MethodChain> entry : map.entrySet()) {
            MethodChainKey key = entry.getKey();
            MethodChain value = entry.getValue();
            MethodChain methodChain = new MethodChain();
            for (CallChainDo callRecordDo : value.getCallRecordDos()) {
                if (callRecordDo.getTime() > time) {
                    methodChain.getCallRecordDos().add(callRecordDo);
                }
            }
            result.put(key, methodChain);
        }
        return result;
    }

    public static long getMaxTime(Map<MethodChainKey, MethodChain> map) {
        long maxTime = 0;
        for (Map.Entry<MethodChainKey, MethodChain> entry : map.entrySet()) {
            MethodChain value = entry.getValue();
            for (CallChainDo callRecordDo : value.getCallRecordDos()) {
                if (callRecordDo.getTime() > maxTime) {
                    maxTime = callRecordDo.getTime();
                }
            }
        }
        return maxTime;
    }
}
