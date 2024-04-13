package dev.mayuna.sakuyabridge.server.v1.tcp;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.base.EndpointConfig;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.base.TimeStopServer;

public class ManagedTimeStopServer extends TimeStopServer {

    /**
     * Creates a new server with the given endpoint config
     *
     * @param endpointConfig Endpoint config
     */
    public ManagedTimeStopServer(EndpointConfig endpointConfig) {
        super(endpointConfig);
    }

    @Override
    protected Connection newConnection() {
        return new ManagedTimeStopConnection(getListenerManager(), getTranslatorManager());
    }
}
