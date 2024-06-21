package dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Listener for the server info packet
 */
public final class ServerInfoListener extends EncryptedListener<Packets.Requests.FetchServerInfo> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ServerInfoListener.class);

    /**
     * Creates a new listener
     */
    public ServerInfoListener() {
        super(Packets.Requests.FetchServerInfo.class);
    }

    @Override
    public void process(SakuyaBridgeConnection connection, Packets.Requests.FetchServerInfo request) {
        LOGGER.mdebug("[" + connection + "] Requested server info");
        connection.sendTCP(new Packets.Responses.FetchServerInfo(SakuyaBridge.INSTANCE.getConfig().getServerInfo()).withResponseTo(request));
    }
}
