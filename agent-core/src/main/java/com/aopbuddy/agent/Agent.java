package com.aopbuddy.agent;


import static com.aopbuddy.infrastructure.Constants.AGENT_JAR_NAME;

import com.aopbuddy.loader.ApoAgentClassloader;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.util.jar.JarFile;
import lombok.SneakyThrows;


public class Agent {

  private static final String ARTHAS_BOOTSTRAP = "com.aopbuddy.agent.BootStrap";
  private static ClassLoader AGENT_LOADER;
  private static final String SPY_API_JAR = "spy-api";


  public static void premain(String agent, Instrumentation instrumentation) {
    startEarthBootstrap(agent, instrumentation);
  }

  public static void agentmain(String agent, Instrumentation instrumentation) {
    startEarthBootstrap(agent, instrumentation);
  }


  private static void startEarthBootstrap(String args, Instrumentation instrumentation) {
    if (AGENT_LOADER != null) {
      return;
    }
    try {
      initSpy(instrumentation);
//      Class.forName("java.aopbuddy.SpyAPI");
      CodeSource codeSource = Agent.class.getProtectionDomain().getCodeSource();
      File arthasAgentJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
      File arthasCoreJarFile = new File(arthasAgentJarFile.getParentFile(), AGENT_JAR_NAME);
      AGENT_LOADER = getClassLoader(arthasCoreJarFile);
      Class<?> bootstrapClass = AGENT_LOADER.loadClass(ARTHAS_BOOTSTRAP);
      bootstrapClass.getMethod("start", Instrumentation.class, String.class)
          .invoke(null, instrumentation, args);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
    String s = "ddd";
  }

  private static ClassLoader getClassLoader(File arthasCoreJarFile)
      throws Throwable {
    // 构造自定义的类加载器，尽量减少对现有工程的侵蚀
    return loadOrDefineClassLoader(arthasCoreJarFile);
  }

  private static ClassLoader loadOrDefineClassLoader(File arthasCoreJarFile) throws Throwable {
    return new ApoAgentClassloader(
        new URL[]{arthasCoreJarFile.toURI().toURL()});
  }

  @SneakyThrows
  public static void initSpy(Instrumentation instrumentation){
    // 将Spy添加到BootstrapClassLoader
    ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
    Class<?> spyClass = null;
    if (parent != null) {
      try {
        spyClass = parent.loadClass("java.arthas.SpyAPI");
      } catch (Throwable e) {
        // ignore
      }
    }
    if (spyClass == null) {
      CodeSource codeSource = BootStrap.class.getProtectionDomain().getCodeSource();
      if (codeSource != null) {
        File arthasCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
        File spyJarFile = findSpyApiJar(arthasCoreJarFile.getParentFile());
        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
      } else {
        throw new IllegalStateException("can not find spy-api jar");
      }
    }
  }

  private static File findSpyApiJar(File dir) {
    File[] files = dir.listFiles(pathname -> {
      String name = pathname.getName().toLowerCase();
      return pathname.isFile() && name.contains(SPY_API_JAR) && name.endsWith(".jar");
    });
    if (files == null || files.length == 0) {
      throw new IllegalStateException("spy-api jar not found in directory: " + dir);
    }
    File latest = files[0];
    for (int i = 1; i < files.length; i++) {
      if (files[i].lastModified() > latest.lastModified()) {
        latest = files[i];
      }
    }
    return latest;
  }
}
