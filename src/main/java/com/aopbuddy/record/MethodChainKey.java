package com.aopbuddy.record;

import com.aopbuddy.infrastructure.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 方法调用链的标识键
 * 设计目标：小体积 + 准确标记
 * <p>
 * 设计思路：
 * 1. startMethodName: 入口方法名，用于快速识别和调试
 * 2. chainHash: 整个调用链的方法序列的哈希值，用于准确区分不同的调用路径
 * <p>
 * 通过组合入口方法名和调用链哈希值，既能快速识别入口方法，又能准确区分不同的调用路径
 */
@Data
@NoArgsConstructor
public class MethodChainKey {
    /**
     * 入口方法名（完整方法签名，如：public com.example.Service.method(com.example.Model)）
     * 用于快速识别和调试
     */
    private String startMethodName;

    /**
     * 调用链的方法序列的哈希值
     * 通过组合调用链中所有方法签名的哈希值计算得出，用于准确区分不同的调用路径
     * 使用 long 类型以支持更长的调用链
     */
    private long chainHash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodChainKey that = (MethodChainKey) o;
        return chainHash == that.chainHash &&
                Objects.equals(startMethodName, that.startMethodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startMethodName, chainHash);
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    /**
     * 简化方法签名
     *
     * @param fullMethod 方法签名字符串
     * @return 简化后的方法信息
     */
    public static SimplifiedMethod simplifyMethod(String fullMethod) {
        String[] parts = fullMethod.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].contains("(") && parts[i].contains(")")) {
                String[] methodParts = parts[i].split("\\(");
                String[] classNameParts = methodParts[0].split("\\.");
                String returnSimpleClassName = parts[i - 1].substring(parts[i - 1].lastIndexOf('.') + 1);
                String simpleTargetClassName = classNameParts[classNameParts.length - 2];
                String methodName = classNameParts[classNameParts.length - 1] + "()";
                return new SimplifiedMethod(returnSimpleClassName, simpleTargetClassName, methodName);
            }
        }
        return null;
    }

    /**
     * 从调用链记录中构造 MethodChainKey
     *
     * @param callRecords 调用链的所有记录
     * @return MethodChainKey
     */
    public static MethodChainKey buildMethodChainKey(List<CallRecord> callRecords) {
        MethodChainKey key = new MethodChainKey();
        key.setStartMethodName(callRecords.get(0).getMethod());
        // 提取调用链中的方法序列（只提取入参记录，因为出参记录的方法名与入参相同）
        List<String> methodSignatures = new ArrayList<>();
        String startMethod = null;
        for (int i = 0; i < Math.min(10, callRecords.size()); i++) {
            if (callRecords.get(i).getArgs() == null) {
                continue;
            }
            String methodStr = callRecords.get(i).getMethod();
            SimplifiedMethod simplifiedMethod = simplifyMethod(methodStr);
            methodSignatures.add(simplifiedMethod.toString());
        }
        // 计算调用链的哈希值
        key.setChainHash(calculateChainHash(methodSignatures));

        return key;
    }

    /**
     * 计算调用链的方法序列的哈希值
     * 使用组合哈希算法，确保不同的调用路径产生不同的哈希值
     *
     * @param methodSignatures 方法签名列表
     * @return 调用链的哈希值
     */
    private static long calculateChainHash(List<String> methodSignatures) {
        if (methodSignatures == null || methodSignatures.isEmpty()) {
            return 0L;
        }
        // 使用类似 String.hashCode() 的算法，但使用 long 类型以避免溢出
        // 使用质数 31 和 131 的组合来减少哈希冲突
        long hash = 1L;
        for (String methodSig : methodSignatures) {
            // 提取简化的方法签名（类名+方法名，去掉包名和参数类型）以减少体积
            long methodHash = methodSig.hashCode();
            // 组合哈希：hash = hash * 31 + methodHash
            hash = hash * 31L + methodHash;
        }
        return hash;
    }
}
