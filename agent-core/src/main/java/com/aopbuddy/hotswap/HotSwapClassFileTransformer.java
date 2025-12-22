package com.aopbuddy.hotswap;

import com.aopbuddy.infrastructure.LoggerFactory;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HotSwapClassFileTransformer implements ClassFileTransformer {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      HotSwapClassFileTransformer.class.getName(),
      LoggerFactory.LogFile.WEB);

  private Map<String, byte[]> classMap;

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer)
      throws IllegalClassFormatException {
    if (className == null) {
      return null;
    }
    try {
      className = className.replace("/", ".");
      LOGGER.info("transform class" + className);
      if (classMap.containsKey(className)) {
        LOGGER.info("retransform class" + className);
        return classMap.get(className);
      }
    } catch (Throwable e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException(e);
    }
    return null;
  }
}
