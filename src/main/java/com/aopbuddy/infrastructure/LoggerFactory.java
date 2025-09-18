package com.aopbuddy.infrastructure;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerFactory {
    private final static FileHandler fileHandler;

    static {
        String userHome = System.getProperty("user.home");
        File logFile = new File(userHome, "logs/weaving.log");
        logFile.getParentFile().mkdirs();
        try {
            fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
            fileHandler.setFormatter(new MyCustomFormatter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Logger getLogger(String name) {
        Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.addHandler(fileHandler);
        return logger;
    }

    public static class MyCustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("%1$tF %1$tT [%2$s] %3$s %n",
                    new java.util.Date(record.getMillis()),
                    record.getLevel().getLocalizedName(),
                    record.getMessage());
        }
    }
}
