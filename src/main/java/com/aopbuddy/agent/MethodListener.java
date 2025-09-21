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
    public static Logger LOGGER = LoggerFactory.getLogger(MethodListener.class.getName(), LISTEN);

    @Override
    public void before(Object target, Method method, Object[] args) {
        LOGGER.info("[Listener] aaabefore " + method + " target=" + target + " args=" + JSONUtil.toJsonStr(args));
    }

    @Override
    public MockedReturnValue after(Object target, Method method, Object[] args, Object returnValue) {
        LOGGER.info("[Listener] after " + method + " target=" + target + " args=" + JSONUtil.toJsonStr(args) + " ret=" + returnValue);
        return new MockedReturnValue(false, "noMock");
    }

    @Override
    public void onException(Object target, Method method, Object[] args, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "Method exception: " + method + " args=" + JSONUtil.toJsonStr(args), throwable);
    }
}
