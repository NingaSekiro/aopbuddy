package com.aopbuddy.groovy;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SimpleClassFileTransformer implements ClassFileTransformer {

  private byte[] classBuffer;
  private String className;

  public SimpleClassFileTransformer(String className, byte[] classBuffer) {
    this.className = className.replace('.', '/');
    this.classBuffer = classBuffer;
  }

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer)
      throws IllegalClassFormatException {

    if (className.equals(this.className)) {
      return classBuffer;
    }

    return null;

  }

}