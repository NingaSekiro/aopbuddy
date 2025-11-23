package com.aopbuddy.agent;

import com.aopbuddy.bytekit.MethodInfo;
import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.MockedReturnValue;
import com.aopbuddy.retransform.Listener;

import java.lang.reflect.Method;


public class MethodListener implements Listener {


    @Override
    public void before(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args) {
        System.out.println("[Listener] before " + methodInfo.getMethodAccess() + "." + methodInfo.getMethodName() + " target=" + target + " args=" + JsonUtil.toJson(args));
    }

    @Override
    public void after(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args, Object returnValue) {
        System.out.println("[Listener] after " + methodInfo.getMethodAccess() + "." + methodInfo.getMethodName() + " target=" + target + " args=" + JsonUtil.toJson(args) + " ret=" + returnValue);
    }

    @Override
        public void onException(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args, Throwable throwable) {
        System.out.println("[Listener] onException " + methodInfo.getMethodAccess() + "." + methodInfo.getMethodName() + " target=" + target + " args=" + JsonUtil.toJson(args) + " throwable=" + throwable);
    }
}
