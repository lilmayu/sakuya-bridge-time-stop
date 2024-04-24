package dev.mayuna.sakuyabridge.commons.v2.logging;

import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.jacoco.Generated;

/**
 * Kryo logger wrapper
 */
@Generated
public final class KryoLogger extends Log.Logger {

    private final SakuyaBridgeLogger logger;

    /**
     * Creates a new Kryo logger
     *
     * @param logger The logger to log to
     */
    public KryoLogger(SakuyaBridgeLogger logger) {
        this.logger = logger;
    }

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        message = "[" + category + "] " + message;

        switch (level) {
            case Log.LEVEL_TRACE:
                logger.trace(message, ex);
                break;
            case Log.LEVEL_DEBUG:
                logger.mdebug(message, ex);
                break;
            case Log.LEVEL_INFO:
                logger.info(message, ex);
                break;
            case Log.LEVEL_WARN:
                logger.warn(message, ex);
                break;
            case Log.LEVEL_ERROR:
                logger.error(message, ex);
                break;
        }
    }
}
