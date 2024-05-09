package dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic;

import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.server.v2.networking.AuthenticatedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Ping listener
 */
public class PingListener extends AuthenticatedListener<Packets.Requests.Ping> {

    /**
     * Creates a new ping listener
     */
    public PingListener() {
        super(Packets.Requests.Ping.class);
    }

    @Override
    public void processAuthenticated(SakuyaBridgeConnection connection, Packets.Requests.Ping message) {
        connection.sendTCP(new Packets.Responses.Pong(System.currentTimeMillis()).withResponseTo(message));
    }
}
