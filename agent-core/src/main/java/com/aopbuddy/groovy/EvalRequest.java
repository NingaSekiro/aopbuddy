package com.aopbuddy.groovy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalRequest {
    private String serverName;
    private String classloader;
    private String script;
}
