package com.aopbuddy.infrastructure;

import com.aopbuddy.record.MethodChainKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtil {

//    public static String toJson(Object obj) {
//        return JSONUtil.toJsonStr(obj);
//    }
//
//    public static String toJson(Object... obj) {
//        return JSONUtil.toJsonStr(obj);
//    }
//
//
//    public static <T> T parse(String json, Class<T> clazz) {
//        return JSONUtil.toBean(json, clazz);
//    }
//
//    public static <T> List<T> parseArray(String json, Class<T> clazz) {
//        return JSONUtil.toList(json, clazz);
//    }

//    public static String toJson(Object obj) {
//        return JSON.toJSONString(obj, JSONWriter.Feature.FieldBased);
//    }
//
//    public static String toJson(Object... obj) {
//        return JSON.toJSONString(obj);
//    }
//
//
//    public static <T> T parse(String json, Class<T> clazz) {
//        return JSON.parseObject(json, clazz);
//    }
//
//    public static <T> List<T> parseArray(String json, Class<T> clazz) {
//        return JSON.parseArray(json, clazz);
//    }


    public static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(MethodChainKey.class, new MethodChainKeyDeserializer());
        objectMapper.registerModule(module);
        // Disable FAIL_ON_EMPTY_BEANS to handle classes without serializable properties
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object... obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> T parse(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}