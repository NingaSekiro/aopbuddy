package com.aopbuddy.aspect;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MethodPointcut extends Pointcut {

  /**
   * 支持通配符 com..MyBean com.*.MyBean
   */
  private String className;

  private String methodName;
  private String parameterTypes;

  public static MethodPointcut of(String className, String methodName, String methodDesc) {
    return new MethodPointcut(className, methodName, methodDesc);
  }

  @Override
  public boolean matchesClassName(String className) {
    return PointcutParser.of(this).isClass(className);
  }

  @Override
  public boolean matchesMethodName(String methodName) {
    if ("*".equals(this.methodName)) {
      return true;
    }
    return PointcutParser.of(this).isMethodName(methodName);
  }
}
