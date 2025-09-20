package com.aopbuddy.agent;

import cn.hutool.json.JSONUtil;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.retransform.Listener;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.aopbuddy.infrastructure.LoggerFactory.LogFile.LISTEN;

public class MethodListener implements Listener {

    @Override
    public void before(Object target, Method method, Object[] args) {
        LoggerFactory.LOGGER.info("[Listener] aaabefore " + method + " target=" + target + " args=" + JSONUtil.toJsonStr(args));
    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        LoggerFactory.LOGGER.info("[Listener] after " + method + " target=" + target + " args=" + JSONUtil.toJsonStr(args) + " ret=" + returnValue);
        return new MockedReturnValue(false, "noMock");
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        LoggerFactory.LOGGER.log(Level.SEVERE, "Method exception: " + method + " args=" + JSONUtil.toJsonStr(args), throwable);
    }
}
