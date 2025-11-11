package com.aopbuddy.infrastructure;

import com.aopbuddy.vmtool.VmToolCommand;
import com.taobao.arthas.common.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


public class ProcessUtils {
    public static long select() throws InputMismatchException {
        Map<Long, String> processMap = listProcessByJps();

        if (processMap.isEmpty()) {
            AnsiLog.info("Can not find java process. Try to run `jps` command lists the instrumented Java HotSpot VMs on the target system.");
            return -1;
        }

        AnsiLog.info("Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.");
        // print list
        int count = 1;
        for (String process : processMap.values()) {
            if (count == 1) {
                System.out.println("* [" + count + "]: " + process);
            } else {
                System.out.println("  [" + count + "]: " + process);
            }
            count++;
        }

        // read choice
        String line = new Scanner(System.in).nextLine();
        if (line.trim().isEmpty()) {
            // get the first process id
            return processMap.keySet().iterator().next();
        }

        int choice = new Scanner(line).nextInt();

        if (choice <= 0 || choice > processMap.size()) {
            return -1;
        }

        Iterator<Long> idIter = processMap.keySet().iterator();
        for (int i = 1; i <= choice; ++i) {
            if (i == choice) {
                return idIter.next();
            }
            idIter.next();
        }

        return -1;
    }


    public static String getToolsClasspath() {
        if (OSUtils.isLinux()) {
            return "tools-1.jar";
        } else if (OSUtils.isWindows()) {
            return "tools-2.jar";
        } else if (OSUtils.isMac()) {
            return "tools-3.jar";
        } else {
            return "tools-1.jar";
        }
    }


    public static boolean addToolsJarToClasspath() {
        if (JavaVersionUtils.isGreaterThanJava8()) {
            return true;
        }
        // 1. 获取JAVA_HOME环境变量
        String javaHome = System.getenv("JAVA_HOME");
        File toolsJar = null;
        if (javaHome != null) {
            toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }
        }
        try {
            URL toolsJarUrl;
            if (toolsJar == null || !toolsJar.exists()) {
//                如果资源在 JAR 文件中：jar:file:/path/to/your-app.jar!/lib/tools.jar（嵌套 JAR URL）,不满足要求。
//                toolsJarUrl = VmToolCommand.class.getResource("/lib/" + getToolsClasspath());
                Path tempFile = Files.createTempFile("ToolsJar", null);
                Files.copy(Objects.requireNonNull(VmToolCommand.class.getResourceAsStream("/lib/" + getToolsClasspath())), tempFile, StandardCopyOption.REPLACE_EXISTING);
                tempFile.toFile().deleteOnExit();
                toolsJarUrl = tempFile.toUri().toURL();
            } else {
                toolsJarUrl = toolsJar.toURI().toURL();
            }
            System.out.println("add tools.jar to classpath: " + toolsJarUrl);
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
            addUrlMethod.invoke(classLoader, toolsJarUrl);
            return true;
        } catch (Exception e) {
            System.err.println("NO JAVA_TOOL: " + e);
            return false;
        }
    }

    private static Map<Long, String> listProcessByJps() {
        Map<Long, String> result = new LinkedHashMap<>();

        String jps = "jps";
        File jpsFile = findJps();
        if (jpsFile != null) {
            jps = jpsFile.getAbsolutePath();
        }

        AnsiLog.debug("Try use jps to list java process, jps: " + jps);

        String[] command = null;

        command = new String[]{jps, "-l"};

        List<String> lines = ExecutingCommand.runNative(command);

        AnsiLog.debug("jps result: " + lines);

        long currentPid = Long.parseLong(PidUtils.currentPid());
        for (String line : lines) {
            String[] strings = line.trim().split("\\s+");
            if (strings.length < 1) {
                continue;
            }
            try {
                long pid = Long.parseLong(strings[0]);
                if (pid == currentPid) {
                    continue;
                }
                if (strings.length >= 2 && isJpsProcess(strings[1])) { // skip jps
                    continue;
                }

                result.put(pid, line);
            } catch (Throwable e) {
                // https://github.com/alibaba/arthas/issues/970
                // ignore
            }
        }

        return result;
    }


    private static File findJps() {
        // Try to find jps under java.home and System env JAVA_HOME
        String javaHome = System.getProperty("java.home");
        String[] paths = {"bin/jps", "bin/jps.exe", "../bin/jps", "../bin/jps.exe"};

        List<File> jpsList = new ArrayList<>();
        for (String path : paths) {
            File jpsFile = new File(javaHome, path);
            if (jpsFile.exists()) {
                AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
                jpsList.add(jpsFile);
            }
        }

        if (jpsList.isEmpty()) {
            AnsiLog.debug("Can not find jps under :" + javaHome);
            String javaHomeEnv = System.getenv("JAVA_HOME");
            AnsiLog.debug("Try to find jps under env JAVA_HOME :" + javaHomeEnv);
            for (String path : paths) {
                File jpsFile = new File(javaHomeEnv, path);
                if (jpsFile.exists()) {
                    AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
                    jpsList.add(jpsFile);
                }
            }
        }

        if (jpsList.isEmpty()) {
            AnsiLog.debug("Can not find jps under current java home: " + javaHome);
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        if (jpsList.size() > 1) {
            Collections.sort(jpsList, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    try {
                        return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                    } catch (IOException e) {
                        // ignore
                    }
                    return -1;
                }
            });
        }
        return jpsList.get(0);
    }

    private static boolean isJpsProcess(String mainClassName) {
        return "sun.tools.jps.Jps".equals(mainClassName) || "jdk.jcmd/sun.tools.jps.Jps".equals(mainClassName);
    }
}
