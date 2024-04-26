package dev.mayuna.sakuyabridge.server.v2.util.pumpk1n;

import dev.mayuna.pumpk1n.util.BaseLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.NonNull;
import org.apache.logging.log4j.Level;

/**
 * A logger that logs to the Sakuya Bridge logger
 */
public final class Pumpk1nLogger extends BaseLogger {

    private final SakuyaBridgeLogger logger;
    private final Level logLevel;

    /**
     * Creates a new Pumpk1n logger
     *
     * @param logger   The Sakuya Bridge logger
     * @param logLevel The log level
     */
    public Pumpk1nLogger(SakuyaBridgeLogger logger, Level logLevel) {
        this.logger = logger;
        this.logLevel = logLevel;
    }

    @Override
    public void log(@NonNull String s, Throwable throwable) {
        logger.log(logLevel, s, throwable);
    }

    /**
     * Enables logging of all types
     *
     * @return This logger
     */
    public Pumpk1nLogger enableAllLogs() {
        logMisc = true;
        logLoad = true;
        logRead = true;
        logWrite = true;
        logCreate = true;

        return this;
    }
}
