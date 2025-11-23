package com.aopbuddy.bytekit;

import com.alibaba.bytekit.asm.binding.Binding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MethodInfo {
    private String className;
    private String methodName;
    private String methodAccess;
    private String methodDesc;
}
