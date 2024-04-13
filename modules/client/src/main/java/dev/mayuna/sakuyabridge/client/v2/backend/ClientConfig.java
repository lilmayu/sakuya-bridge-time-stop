package dev.mayuna.sakuyabridge.client.v2.backend;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.v2.frontend.FrontendConfig;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.timestop.config.EncryptionConfig;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import lombok.Data;

@Data
public final class ClientConfig {

    private static final String CONFIG_FILE_NAME = "graphical_frontend.json";

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FrontendConfig.class);
    private static final Gson GSON = new Gson();

    private EndpointConfig endpointConfig = new EndpointConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();

    /**
     * Loads the settings from the file.
     *
     * @return The settings
     */
    public static ClientConfig load() {
        LOGGER.info("Loading client settings (" + CONFIG_FILE_NAME + ")");
        return ApplicationConfigLoader.loadFrom(GSON, CONFIG_FILE_NAME, ClientConfig.class, false);
    }

    /**
     * Saves the settings to the file.
     */
    public void save() {
        LOGGER.info("Saving client settings (" + CONFIG_FILE_NAME + ")");
        ApplicationConfigLoader.saveTo(GSON, CONFIG_FILE_NAME, this);
    }

}
