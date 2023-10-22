package dev.mayuna.sakuyabridge.commons.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

public class Log4jUtils {

    /**
     * Adds appender to root logger
     *
     * @param appender Appender
     */
    public static void addAppender(Appender appender) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration log4jConfig = ctx.getConfiguration();

        appender.start();
        log4jConfig.getRootLogger().addAppender(appender, null, null);
        ctx.updateLoggers();
    }
}
