package dev.mayuna.sakuyabridge.server.v2;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.timestop.config.EncryptionConfig;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import lombok.Getter;

/**
 * Configuration for the server module
 */
@Getter
public final class Config {

    private static final String CONFIG_FILE = "server-config.json";
    private static final Gson GSON = new Gson();

    private Server server = new Server();

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

    @Getter
    public static final class Server {

        private int port = 28077;
        private EndpointConfig endpointConfig = new EndpointConfig();
        private EncryptionConfig encryptionConfig = new EncryptionConfig();
        private long encryptionTimeoutMillis = 5000;
    }
}
