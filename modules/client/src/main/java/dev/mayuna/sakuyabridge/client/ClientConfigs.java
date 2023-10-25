package dev.mayuna.sakuyabridge.client;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.configs.LoggerConfig;
import dev.mayuna.sakuyabridge.client.configs.ServerConnectConfig;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.config.EncryptionConfig;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.EndpointConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientConfigs {

    private EndpointConfig endpointConfig = new EndpointConfig();
    private LoggerConfig loggerConfig = new LoggerConfig();
    private ServerConnectConfig serverConnectConfig = new ServerConnectConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();

    /**
     * Loads the configuration from the config file
     *
     * @param gson Gson instance
     *
     * @return the configuration
     */
    public static ClientConfigs load(Gson gson) {
        return ApplicationConfigLoader.loadFrom(gson, Constants.CONFIG_FILE_NAME, ClientConfigs.class, false);
    }

    /**
     * Saves the configuration to the config file
     *
     * @param gson Gson instance
     */
    public void save(Gson gson) {
        ApplicationConfigLoader.saveTo(gson, Constants.CONFIG_FILE_NAME, this);
    }
}
