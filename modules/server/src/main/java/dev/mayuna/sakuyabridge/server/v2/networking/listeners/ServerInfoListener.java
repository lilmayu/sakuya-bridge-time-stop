package dev.mayuna.sakuyabridge.server.v2.networking.listeners;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Listener for the server info packet
 */
public final class ServerInfoListener extends EncryptedListener<Packets.Requests.ServerInfo> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ServerInfoListener.class);

    /**
     * Creates a new listener
     */
    public ServerInfoListener() {
        super(Packets.Requests.ServerInfo.class);
    }

    @Override
    public void process(SakuyaBridgeConnection connection, Packets.Requests.ServerInfo message) {
        LOGGER.mdebug("[" + connection + "] Requested server info");
        connection.sendTCP(new Packets.Responses.ServerInfo(SakuyaBridge.INSTANCE.getConfig().getServerInfo()).withResponseTo(message));
    }
}
