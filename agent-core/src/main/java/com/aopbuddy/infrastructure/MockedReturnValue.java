package com.aopbuddy.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MockedReturnValue {
    private boolean isMock=false;
    private Object mockValue;
}
