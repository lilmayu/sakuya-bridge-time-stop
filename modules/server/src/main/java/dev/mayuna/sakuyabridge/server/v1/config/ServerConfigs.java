package dev.mayuna.sakuyabridge.server.v1.config;

import dev.mayuna.sakuyabridge.commons.v1.config.EncryptionConfig;
import dev.mayuna.sakuyabridge.commons.v1.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.base.EndpointConfig;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerConfigs {

    private int serverPort = NetworkConstants.DEFAULT_PORT;

    private EndpointConfig endpointConfig = new EndpointConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();
    private StorageConfig storageConfig = new StorageConfig();
}
