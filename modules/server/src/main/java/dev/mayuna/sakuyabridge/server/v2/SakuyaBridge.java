package dev.mayuna.sakuyabridge.server.v2;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.networking.Server;
import lombok.Getter;

/**
 * The main class for the server module
 */
@Getter
public final class SakuyaBridge {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();

    private Config config;
    private Server server;

    private SakuyaBridge() {
    }

    /**
     * Starts the backend
     */
    public void start() {
        LOGGER.info("Loading config");
        config = Config.load();

        LOGGER.info("Creating server");
        server = new Server(config.getServer());

        LOGGER.info("Starting server");
        server.start();
    }

    /**
     * Stops the backend
     */
    public void stop() {
        LOGGER.info("Stopping server");
        server.stop();
    }
}
