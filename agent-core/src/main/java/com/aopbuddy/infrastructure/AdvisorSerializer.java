package com.aopbuddy.infrastructure;

import com.aopbuddy.retransform.Advisor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AdvisorSerializer extends JsonSerializer<Advisor> {
    @Override
    public void serialize(Advisor advisor, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        
        // 序列化pointcut字段
        gen.writeObjectField("pointcut", advisor.getPointcut());
        
        // 序列化signatures字段
        gen.writeObjectField("signatures", advisor.getSignatures());
        
        // 序列化listener，使用Class名称作为key
        if (advisor.getListener() != null) {
            String listenerName = advisor.getListener().getClass().getName();
            gen.writeObjectField(listenerName, advisor.getListener());
        }
        
        gen.writeEndObject();
    }
}