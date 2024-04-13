package dev.mayuna.sakuyabridge.client.v2.backend;

import dev.mayuna.sakuyabridge.client.v2.backend.networking.Client;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * The backend of SakuyaBridge: Time Stop
 */
@Getter
public final class SakuyaBridge {

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();
    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);

    private ClientConfig config;
    private Client client;

    private SakuyaBridge() {
    }

    /**
     * Starts the backend
     */
    public boolean boot() {
        LOGGER.info("Booting SakuyaBridge");
        reset();

        config = ClientConfig.load();
        client = new Client(config);
        client.start();

        if (!client.isSuccessfullyPrepared()) {
            LOGGER.error("Failed to prepare client");
            return false;
        }

        return true;
    }

    /**
     * Resets the backend
     */
    public void reset() {
        LOGGER.info("Resetting SakuyaBridge");

        if (client != null) {
            LOGGER.info("Stopping client");
            client.stop();
        }
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
        var future = new CompletableFuture<Boolean>();

        LOGGER.info("Connecting to server: " + host);

        CompletableFuture.runAsync(() -> {
            try {
                client.connect(5000, host, 28077);
            } catch (Exception exception) {
                LOGGER.error("Failed to connect to server: " + host, exception);
                future.complete(false);
                return;
            }

            var connectionEncrypted = client.getConnectionSuccessful().join();

            if (!connectionEncrypted) {
                LOGGER.error("Failed to encrypt connection to server: " + host);
                future.complete(false);
                return;
            }

            // TODO: Request ServerInfo
            LOGGER.success("Connected to server: " + host);
            future.complete(true);
        });

        return future;
    }
}
