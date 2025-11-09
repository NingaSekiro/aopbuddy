package com.aopbuddy.retransform;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EnterResult {
    private List<Listener> listeners = new ArrayList<>();

}
