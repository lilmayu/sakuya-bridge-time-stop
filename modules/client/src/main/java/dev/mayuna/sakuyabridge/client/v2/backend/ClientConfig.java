package dev.mayuna.sakuyabridge.client.v2.backend;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.v2.frontend.FrontendConfig;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.SessionToken;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.timestop.config.EncryptionConfig;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public final class ClientConfig {

    private static final String CONFIG_FILE_NAME = "client-config.json";

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FrontendConfig.class);
    private static final Gson GSON = new Gson();

    private EndpointConfig endpointConfig = new EndpointConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();
    private ChatConfig chatConfig = new ChatConfig();
    private int connectionTimeoutMillis = 5000;
    private SessionToken previousSessionToken = null;
    private GameInfo lastGameInfo = null;

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

    /**
     * Clears the previous session token if it is expired.
     */
    public void clearPreviousSessionTokenIfExpired() {
        if (isPreviousSessionTokenExpired()) {
            LOGGER.info("Clearing previous session token because it is expired");
            previousSessionToken = null;
        }
    }

    /**
     * Checks if the previous session token is expired.
     *
     * @return True if the previous session token is expired, otherwise false
     */
    public boolean isPreviousSessionTokenExpired() {
        return previousSessionToken == null || previousSessionToken.isExpired();
    }

    /**
     * Gets the previous session token if it is not expired.
     *
     * @return The previous session token if it is not expired, otherwise null
     */
    public SessionToken getPreviousSessionTokenIfNotExpired() {
        return isPreviousSessionTokenExpired() ? null : previousSessionToken;
    }

    @Getter @Setter
    public final static class ChatConfig {

        private boolean keepTheChatCivilWarning = true;

    }
}
