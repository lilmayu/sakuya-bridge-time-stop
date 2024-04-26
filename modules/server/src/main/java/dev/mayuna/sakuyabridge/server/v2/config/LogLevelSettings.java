package dev.mayuna.sakuyabridge.server.v2.config;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.Getter;
import org.apache.logging.log4j.Level;

/**
 * Configuration for the log levels (used in {@link Config}
 */
@Getter
public enum LogLevelSettings {
    INFO(Level.INFO),
    DEBUG(Level.DEBUG),
    TRACE(Level.TRACE),
    WARN(Level.WARN),
    ERROR(Level.ERROR),
    FATAL(Level.FATAL),
    MDEBUG(SakuyaBridgeLogger.MDEBUG),
    FLOW(SakuyaBridgeLogger.FLOW);

    private final Level log4jLevel;

    LogLevelSettings(Level log4jLevel) {
        this.log4jLevel = log4jLevel;
    }
}