package com.aopbuddy.infrastructure;

import com.aopbuddy.record.MethodChainKey;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class MethodChainKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return JsonUtil.parse(key, MethodChainKey.class);
    }
}
