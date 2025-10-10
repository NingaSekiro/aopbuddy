package com.aopbuddy.record;

import com.aopbuddy.infrastructure.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class MethodChainKey {
    private String startMethodName;
    private List<Integer> lineNums = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodChainKey that = (MethodChainKey) o;
        return startMethodName.equals(that.startMethodName) && lineNums.equals(that.lineNums);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startMethodName, lineNums);
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
