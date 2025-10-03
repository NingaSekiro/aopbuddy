package com.aopbuddy.infrastructure;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

import java.util.List;

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

    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, JSONWriter.Feature.FieldBased);
    }

    public static String toJson(Object... obj) {
        return JSON.toJSONString(obj);
    }


    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
}
