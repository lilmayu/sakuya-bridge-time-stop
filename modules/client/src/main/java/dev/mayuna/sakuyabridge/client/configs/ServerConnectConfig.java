package dev.mayuna.sakuyabridge.client.configs;

import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerConnectConfig {

    private String serverAddress = "127.0.0.1";
    private int serverPort = NetworkConstants.DEFAULT_PORT;
    private int timeoutMillis = 10000;
}
