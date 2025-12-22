package com.aopbuddy.bytekit;


import com.aopbuddy.infrastructure.StringUtils;
import com.aopbuddy.retransform.Context;
import com.aopbuddy.retransform.Listener;
import java.aopbuddy.SpyAPI;
import java.util.List;


public class SpyImpl extends SpyAPI.AbstractSpy {

  @Override
  public void atEnter(Class<?> clazz, String methodInfo, Object target, String[] argNames,
      Object[] args) {
    String[] info = StringUtils.splitMethodInfo(methodInfo);
    String methodName = info[1];
    List<Listener> listeners = Context.getCache(clazz.getName(), methodName);
    if (listeners != null) {
      for (Listener Listener : listeners) {
        Listener.before(target, clazz,
            MethodInfo.builder().className(clazz.getName()).methodName(methodName)
                .methodAccess(info[0]).methodDesc(info[2]).build(), argNames, args);
      }
    }

  }

  @Override
  public Object atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
      Object returnObject) {

    String[] info = StringUtils.splitMethodInfo(methodInfo);
    String methodName = info[1];
    List<Listener> listeners = Context.getCache(clazz.getName(), methodName);

    if (listeners != null) {
      for (Listener Listener : listeners) {
        Object after = Listener.after(target, clazz,
            MethodInfo.builder().className(clazz.getName()).methodName(methodName)
                .methodAccess(info[0]).methodDesc(info[2]).build(), args, returnObject);
        if (after != null) {
          return after;
        }
      }
    }
    return returnObject;
  }

  @Override
  public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
      Throwable throwable) {
    String[] info = StringUtils.splitMethodInfo(methodInfo);
    String methodName = info[1];
    List<Listener> listeners = Context.getCache(clazz.getName(), methodName);
    if (listeners != null) {
      for (Listener Listener : listeners) {
        Listener.onException(target, clazz,
            MethodInfo.builder().className(clazz.getName()).methodName(methodName)
                .methodAccess(info[0]).methodDesc(info[2]).build(), args, throwable);
      }
    }
  }

  @Override
  public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {

  }

  @Override
  public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {

  }

  @Override
  public void atInvokeException(Class<?> clazz, String invokeInfo, Object target,
      Throwable throwable) {

  }

}