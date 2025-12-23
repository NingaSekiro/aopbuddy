package com.aopbuddy.retransform;


import com.aopbuddy.bytekit.MethodInfo;

public interface Listener {

  //
  default void before(Object target, Class<?> clazz, MethodInfo methodInfo, String[] argNames,
      Object[] args) {

  }

  default Object after(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args,
      Object returnValue) {
    return null;
  }

  default void onException(Object target, Class<?> clazz, MethodInfo methodInfo, Object[] args,
      Throwable throwable) {

  }

}
