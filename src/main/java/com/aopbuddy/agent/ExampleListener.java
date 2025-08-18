package com.aopbuddy.agent;

import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.retransform.Listener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ExampleListener implements Listener {
    private static final Logger logger = Logger.getLogger(ExampleListener.class.getName());

    @Override
    public void before(Object target, Method method, Object[] args) {
        logger.info("test jul");
        System.out.println("[Listener] before " + method + " target=" + target + " args=" + java.util.Arrays.toString(args));

    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        System.out.println("[Listener] after " + method + " ret=" + returnValue);
        return new MockedReturnValue(true, "mocked");
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        System.out.println("[Listener] ex " + throwable);
    }
}
