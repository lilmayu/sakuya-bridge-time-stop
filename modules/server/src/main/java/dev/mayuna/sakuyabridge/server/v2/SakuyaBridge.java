package dev.mayuna.sakuyabridge.server.v2;

import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.managers.accounts.AccountManagerBundle;
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

    public AccountManagerBundle accountManagers = new AccountManagerBundle();

    private SakuyaBridge() {
    }

    /**
     * Starts the backend
     */
    public void start() {
        LOGGER.info("Booting SakuyaBridge: Time Stop server");
        LOGGER.info(" = Version: " + CommonConstants.CURRENT_SERVER_VERSION);
        LOGGER.info(" = Network protocol: " + CommonConstants.CURRENT_NETWORK_PROTOCOL);

        LOGGER.info("Loading config");
        config = Config.load();

        LOGGER.info("Creating server");
        Log.setLogger(new KryoLogger(Server.LOGGER));
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
