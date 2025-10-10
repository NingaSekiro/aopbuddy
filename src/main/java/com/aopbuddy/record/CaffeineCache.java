package com.aopbuddy.record;

import com.aopbuddy.infrastructure.MethodChainKeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class CaffeineCache {
    @Getter
    private static final Cache<MethodChainKey, MethodChain> cache;

    static {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 写入后10分钟过期
                .build();
    }

    public static MethodChain get(MethodChainKey key) {
        return cache.getIfPresent(key);
    }

    public static void put(MethodChainKey key, MethodChain methodChain) {
        cache.put(key, methodChain);
    }

}
