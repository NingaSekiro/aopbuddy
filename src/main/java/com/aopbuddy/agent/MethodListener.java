package com.aopbuddy.agent;

import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.retransform.Listener;

import java.lang.reflect.Method;


public class MethodListener implements Listener {

    @Override
    public void before(Object target, Method method, Object[] args) {
        System.out.println("[Listener] aaabefore " + method + " target=" + target + " args=" + JsonUtil.toJson(args));
    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        System.out.println("[Listener] after " + method + " target=" + target + " args=" + JsonUtil.toJson(args) + " ret=" + returnValue);
        return new MockedReturnValue(true, "mocked");
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        System.out.println("[Listener] onException " + method + " target=" + target + " args=" + JsonUtil.toJson(args) + " throwable=" + throwable);
    }
}
