package com.aopbuddy.agent;

import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.retransform.Listener;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.aopbuddy.infrastructure.LoggerFactory.LogFile.LISTEN;

public class MethodListener implements Listener {
    public static Logger LOGGER = LoggerFactory.getLogger(MethodListener.class.getName(), LISTEN);

    @Override
    public void before(Object target, Method method, Object[] args) {
        LOGGER.info("[Listener] aaabefore " + method + " target=" + target + " args=" + JsonUtil.toJson(args));
    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        LOGGER.info("[Listener] after " + method + " target=" + target + " args=" + JsonUtil.toJson(args) + " ret=" + returnValue);
        return new MockedReturnValue(true, "mocked");
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "Method exception: " + method + " args=" + JsonUtil.toJson(args), throwable);
    }
}
