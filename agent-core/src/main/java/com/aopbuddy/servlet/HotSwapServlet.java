package com.aopbuddy.servlet;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import com.aopbuddy.hotswap.HotSwapClassFileTransformer;
import com.aopbuddy.infrastructure.JsonUtil;
import com.aopbuddy.infrastructure.LoggerFactory;
import com.aopbuddy.record.ClassFilePath;
import com.aopbuddy.retransform.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HotSwapServlet implements Action {

  private static final Logger LOGGER = LoggerFactory.getLogger(HotSwapServlet.class.getName(),
      LoggerFactory.LogFile.WEB);

  private static HotSwapClassFileTransformer HOT_SWAP_CLASS_FILE_TRANSFORMER;

  @Override
  public void doAction(HttpServerRequest request, HttpServerResponse response) {
    LOGGER.info("classloader" + HotSwapServlet.class.getClassLoader().getClass().getName());
    List<ClassFilePath> classFilePaths = JsonUtil.parse(request.getBody(),
        new TypeReference<List<ClassFilePath>>() {
        });
    Map<String, byte[]> classMap = classFilePaths.stream()
        .collect(java.util.stream.Collectors.toMap(
            ClassFilePath::getClassName,
            classFilePath -> Base64.getDecoder().decode(classFilePath.getPayload())));
    Instrumentation inst = Context.inst;
    LOGGER.info("Instrumentation" + inst.hashCode());
    if (HOT_SWAP_CLASS_FILE_TRANSFORMER == null) {
      HOT_SWAP_CLASS_FILE_TRANSFORMER = new HotSwapClassFileTransformer(
          classMap);
      inst.addTransformer(HOT_SWAP_CLASS_FILE_TRANSFORMER,true);
    } else {
      HOT_SWAP_CLASS_FILE_TRANSFORMER.getClassMap().putAll(classMap);
    }
    List<Class<?>> classes = new ArrayList<>();
    for (Class<?> allLoadedClass : inst.getAllLoadedClasses()) {
      if (classMap.containsKey(allLoadedClass.getName())) {
        classes.add(allLoadedClass);
      }
    }
    if (CollectionUtil.isEmpty(classes)) {
      return;
    }
    List<String> classNames = classFilePaths.stream().map(ClassFilePath::getClassName)
        .collect(Collectors.toList());
    LOGGER.info("retransformClasses " + classNames);
    try {
      inst.retransformClasses(classes.toArray(new Class[0]));
    } catch (Throwable e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      response.write(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
