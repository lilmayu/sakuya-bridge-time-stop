package dev.mayuna.sakuyabridge.client;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.configs.ServerConnectConfig;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.EndpointConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientConfigs {

    private EndpointConfig endpointConfig = new EndpointConfig();
    private ServerConnectConfig serverConnectConfig = new ServerConnectConfig();

    public static ClientConfigs load(Gson gson) {
        return ApplicationConfigLoader.loadFrom(gson, Constants.CONFIG_FILE_NAME, ClientConfigs.class, false);
    }
}
