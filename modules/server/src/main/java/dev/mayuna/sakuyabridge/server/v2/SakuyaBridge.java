package dev.mayuna.sakuyabridge.server.v2;

import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.managers.accounts.AccountManagerBundle;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.managers.games.GameManager;
import dev.mayuna.sakuyabridge.server.v2.managers.sessions.SessionTokenManager;
import dev.mayuna.sakuyabridge.server.v2.managers.user.UserManager;
import dev.mayuna.sakuyabridge.server.v2.networking.Server;
import lombok.Getter;

/**
 * The main class for the server module
 */
@Getter
public final class SakuyaBridge {

    public static final SakuyaBridge INSTANCE = new SakuyaBridge();
    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridge.class);
    private Config config;
    private Server server;

    private AccountManagerBundle accountManagers;
    private SessionTokenManager sessionTokenManager;
    private UserManager userManager;
    private GameManager gameManager;
    private ChatManager chatManager;

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
        showConfigValues();

        LOGGER.info("Initializing account managers");
        accountManagers = new AccountManagerBundle(config.getAccountManager());
        accountManagers.init();

        LOGGER.info("Initializing session token manager");
        sessionTokenManager = new SessionTokenManager(config.getSessionTokenManager());
        sessionTokenManager.init();

        LOGGER.info("Initializing user manager");
        userManager = new UserManager(config.getUserManager());
        userManager.init();

        LOGGER.info("Initializing chat manager");
        chatManager = new ChatManager(config.getChatManager());
        chatManager.init();

        LOGGER.info("Initializing game manager");
        gameManager = new GameManager(config.getGameManager(), chatManager);
        gameManager.init();

        LOGGER.info("Creating server");
        Log.setLogger(new KryoLogger(Server.LOGGER));
        server = new Server(config.getServer());

        LOGGER.info("Starting server");
        server.start();

        LOGGER.info("Post-server startup actions...");
        postServerStartup();

        LOGGER.success("Sakuya Bridge started successfully");
    }

    /**
     * Shows the config values in the log
     */
    private void showConfigValues() {
        if (config.isPrintConfigAtStart()) {
            LOGGER.mdebug("{}", config);
        }

        // Specific
        if (config.getServerInfo().isRegisterEnabled()) {
            LOGGER.info("Registration is enabled; users can create accounts.");
        } else {
            LOGGER.warn("Registration is disabled; users cannot create accounts.");
        }
    }

    /**
     * Post server startup actions
     */
    private void postServerStartup() {
        gameManager.registerConnectionListener();
    }

    /**
     * Stops the backend
     */
    public void stop() {
        LOGGER.info("Stopping server");
        server.stop();

        // Shutdown account managers
        accountManagers.shutdown();

        // Shutdown session token manager
        sessionTokenManager.shutdown();

        // Shutdown user manager
        userManager.shutdown();
    }
}
