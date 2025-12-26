package com.aopbuddy.infrastructure;

import java.aopbuddy.SpyAPI;

public class ThreadUtil {

  public static boolean findTheSpyAPIDepth(StackTraceElement[] stackTraceElementArray) {
    int from = Math.max(0, stackTraceElementArray.length - 20);
    for (int i = stackTraceElementArray.length - 1; i >= from; --i) {
      if (SpyAPI.class.getName().equals(stackTraceElementArray[i].getClassName())) {
        return true;
      }
    }
    return false;
  }

}
