package dev.mayuna.sakuyabridge.server.v2.config;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.timestop.config.EncryptionConfig;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import lombok.Getter;

/**
 * Configuration for the server module
 */
@Getter
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Config {

    private static final String CONFIG_FILE = "server-config.json";
    private static final Gson GSON = new Gson();

    private Server server = new Server();
    private ServerInfo serverInfo = new ServerInfo();
    private AccountManager accountManager = new AccountManager();
    private SessionTokenManager sessionTokenManager = new SessionTokenManager();
    private UserManager userManager = new UserManager();

    /**
     * Loads the configuration from the config file
     *
     * @return The loaded configuration
     */
    public static Config load() {
        return ApplicationConfigLoader.loadFrom(GSON, CONFIG_FILE, Config.class, true);
    }

    /**
     * Saves the configuration to the config file
     */
    public void save() {
        ApplicationConfigLoader.saveTo(GSON, CONFIG_FILE, this);
    }

    /**
     * The server settings
     */
    @Getter
    public static final class Server {

        private int port = 28077;
        private EndpointConfig endpointConfig = new EndpointConfig();
        private EncryptionConfig encryptionConfig = new EncryptionConfig();
        private long encryptionTimeoutMillis = 5000;
    }

    /**
     * The account manager settings
     */
    @Getter
    public static final class AccountManager {

        private boolean recreatePumpk1nAccountListOnStartup = false;
        private StorageSettings storageSettings = new StorageSettings("accounts");
    }

    /**
     * The settings for the session token manager
     */
    @Getter
    public static final class SessionTokenManager {

        private long sessionLifespanMillis = 604800000;
        private StorageSettings storageSettings = new StorageSettings("sessions");
    }

    @Getter
    public static final class UserManager {

        private StorageSettings storageSettings = new StorageSettings("users");
    }
}