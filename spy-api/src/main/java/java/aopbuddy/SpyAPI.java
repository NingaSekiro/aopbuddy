package java.aopbuddy;

public class SpyAPI {

  public static final AbstractSpy NOPSPY = new NopSpy();
  private static volatile AbstractSpy spyInstance = NOPSPY;

  public static volatile boolean INITED;

  public static AbstractSpy getSpy() {
    return spyInstance;
  }

  public static void setSpy(AbstractSpy spy) {
    spyInstance = spy;
  }

  public static void setNopSpy() {
    setSpy(NOPSPY);
  }

  public static boolean isNopSpy() {
    return NOPSPY == spyInstance;
  }

  public static void init() {
    INITED = true;
  }

  public static boolean isInited() {
    return INITED;
  }

  public static void destroy() {
    setNopSpy();
    INITED = false;
  }

  public static void atEnter(Class<?> clazz, String methodInfo, Object target, String[] argNames,
      Object[] args) {
    spyInstance.atEnter(clazz, methodInfo, target, argNames, args);
  }

  public static Object atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
      Object returnObject) {
    return spyInstance.atExit(clazz, methodInfo, target, args, returnObject);
  }

  public static void atExceptionExit(Class<?> clazz, String methodInfo, Object target,
      Object[] args, Throwable throwable) {
    spyInstance.atExceptionExit(clazz, methodInfo, target, args, throwable);
  }

  public static void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {
    spyInstance.atBeforeInvoke(clazz, invokeInfo, target);
  }

  public static void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {
    spyInstance.atAfterInvoke(clazz, invokeInfo, target);
  }

  public static void atInvokeException(Class<?> clazz, String invokeInfo, Object target,
      Throwable throwable) {
    spyInstance.atInvokeException(clazz, invokeInfo, target, throwable);
  }

  public static abstract class AbstractSpy {

    public abstract void atEnter(Class<?> clazz, String methodInfo, Object target,
        String[] argNames,
        Object[] args);

    public abstract Object atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
        Object returnObject);

    public abstract void atExceptionExit(Class<?> clazz, String methodInfo, Object target,
        Object[] args, Throwable throwable);

    public abstract void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target);

    public abstract void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target);

    public abstract void atInvokeException(Class<?> clazz, String invokeInfo, Object target,
        Throwable throwable);
  }

  static class NopSpy extends AbstractSpy {

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, String[] argNames,
        Object[] args) {
    }

    @Override
    public Object atExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
        Object returnObject) {
      return returnObject;
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args,
        Throwable throwable) {
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
}
