package com.aopbuddy.retransform;


import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.alibaba.bytekit.asm.interceptor.annotation.ExceptionHandler;
import com.alibaba.bytekit.utils.Decompiler;
import com.aopbuddytest.TestHelper;
import org.junit.jupiter.api.Test;


public class AtExitTest {

  static class Sample {

    long longField;
    int intField;
    String strField;

    public void voidExit() {

    }

    public long longExit() {
      return 100L;
    }

    public static long staticExit() {
      return 999L;
    }
  }

  public static class TestPrintSuppressHandler {

    @ExceptionHandler(inline = false)
    public static void onSuppress(@Binding.Throwable Throwable e, @Binding.Class Object clazz) {
      System.err.println("exception handler: " + clazz);
      e.printStackTrace();
    }
  }

  public static class TestAccessInterceptor {

    @AtExit(inline = false)
    public static void atExit(@Binding.This Object object,
        @Binding.Class Object clazz
        ,
        @Binding.Return Object re
    ) {
      System.err.println("AtFieldAccess: this" + object);
    }
  }

  public static class ChangeReturnInterceptor {

    @AtExit(inline = false, suppress = RuntimeException.class, suppressHandler = TestPrintSuppressHandler.class)
    public static Object onExit(@Binding.This Object object, @Binding.Class Object clazz) {
      System.err.println("onExit, object:" + object);
      return 123L;
    }
  }

  @Test
  public void testExit() throws Exception {
    TestHelper helper = TestHelper.builder().interceptorClass(TestAccessInterceptor.class)
        .methodMatcher("voidExit")
        .reTransform(true);
    byte[] bytes = helper.process(Sample.class);

    new Sample().voidExit();

    System.err.println(Decompiler.decompile(bytes));

  }


  @Test
  public void testExitAndChangeReturn() throws Exception {

    TestHelper helper = TestHelper.builder().interceptorClass(ChangeReturnInterceptor.class)
        .methodMatcher("longExit")
        .reTransform(true);
    byte[] bytes = helper.process(Sample.class);

    System.err.println(Decompiler.decompile(bytes));

    long re = new Sample().longExit();
    assertEquals(123L, re);

  }
}
