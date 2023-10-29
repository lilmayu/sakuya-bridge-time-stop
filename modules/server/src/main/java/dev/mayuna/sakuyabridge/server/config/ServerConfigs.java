package dev.mayuna.sakuyabridge.server.config;

import dev.mayuna.sakuyabridge.commons.config.EncryptionConfig;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.EndpointConfig;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerConfigs {

    private int serverPort = NetworkConstants.DEFAULT_PORT;

    private EndpointConfig endpointConfig = new EndpointConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();
    private StorageConfig storageConfig = new StorageConfig();
}
