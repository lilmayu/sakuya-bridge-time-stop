package dev.mayuna.sakuyabridge.client.v2.backend;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * The backend of SakuyaBridge: Time Stop
 */
@Getter
public final class SakuyaBridge {

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();
    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);

    private SakuyaBridge() {
    }

    /**
     * Starts the backend
     */
    public void boot() {
        LOGGER.info("Booting SakuyaBridge");

        reset();
    }

    /**
     * Resets the backend
     */
    public void reset() {
        LOGGER.info("Resetting SakuyaBridge");
    }

    /**
     * Connects to the server
     *
     * @param host The host to connect to
     *
     * @return A future that completes when the connection is established
     */
    public CompletableFuture<Boolean> connectToServer(String host) {
        // todo: return SakuyaBridgeServerInfo object, which will hold the server name, motd, login methods, etc.

        LOGGER.info("Connecting to server: " + host);
        return CompletableFuture.completedFuture(true);
    }
}
