package com.aopbuddy.infrastructure;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.*;

import static com.aopbuddy.infrastructure.LoggerFactory.LogFile.LISTEN;

public class LoggerFactory {


    public enum LogFile {
        WEAVING("weaving.log"),
        LISTEN("listen.log"),
        WEB("web.log"),
        ;

        public final String fileName;

        LogFile(String fileName) {
            this.fileName = fileName;
        }
    }

    @Getter
    private static final File logDir = determineLogDir();
    private static final Map<LogFile, FileHandler> handlers = new EnumMap<>(LogFile.class);

    static {
        // 初始化所有日志处理器
        for (LogFile logFile : LogFile.values()) {
            try {
                FileHandler handler = new FileHandler(new File(logDir, logFile.fileName).getAbsolutePath(), true);
                handler.setFormatter(new SimpleFormatter());
                handlers.put(logFile, handler);
            } catch (IOException e) {
                throw new RuntimeException("初始化日志处理器失败: " + logFile.fileName, e);
            }
        }
    }

    /**
     * 确定日志目录，优先用户目录，无权限则使用临时目录
     */
    private static File determineLogDir() {
        // 尝试用户主目录下的logs文件夹
        File userLogDir = new File(System.getProperty("user.home"), "logs");
        try {
            // 确保目录存在
            if (!userLogDir.exists() && !userLogDir.mkdirs()) {
                throw new IOException("无法创建用户日志目录");
            }

            // 测试写入权限
            File testFile = new File(userLogDir, ".permission_test");
            if (!testFile.createNewFile()) {
                throw new IOException("无法在用户日志目录创建测试文件");
            }
            testFile.delete(); // 清理测试文件
            return userLogDir;
        } catch (Exception e) {
            // 切换到临时目录
            File tempLogDir = new File(System.getProperty("java.io.tmpdir"), "logs");
            tempLogDir.mkdirs();
            return tempLogDir;
        }
    }

    @SneakyThrows
    public static Logger getLogger(String name, LogFile logFile) {
        Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.addHandler(handlers.get(logFile));
        return logger;
    }

    public static Logger getLogger(String name) {
        return getLogger(name, LogFile.WEAVING);
    }

    public static class MyCustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%1$tF %1$tT [%2$s] %3$s %n",
                    new Date(record.getMillis()),
                    record.getLevel().getLocalizedName(),
                    record.getMessage());
        }
    }
}
