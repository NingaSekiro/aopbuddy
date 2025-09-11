package com.aopbuddy.groovy;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvalRequest {
    private String pid;
    private String classloader;
    private String script;
}
