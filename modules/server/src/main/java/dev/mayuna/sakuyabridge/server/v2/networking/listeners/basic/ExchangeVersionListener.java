package dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import lombok.NonNull;

public final class ExchangeVersionListener extends EncryptedListener<Packets.Requests.VersionExchange> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ExchangeVersionListener.class);

    /**
     * Creates a new listener
     */
    public ExchangeVersionListener() {
        super(Packets.Requests.VersionExchange.class);
    }

    @Override
    public void process(SakuyaBridgeConnection connection, Packets.Requests.@NonNull VersionExchange message) {
        int clientVersion = message.getClientVersion();
        int networkProtocolVersion = message.getNetworkProtocolVersion();

        // Check if the client version is valid
        if (clientVersion < 0) {
            LOGGER.mdebug("[" + connection + "] Invalid client version: " + clientVersion + " - sending error and disconnecting.");

            synchronized (connection) {
                connection.sendTCP(createResponsePacket().withError("Invalid client version").withResponseTo(message));
                connection.close();
            }
            return;
        }

        // Check if the network protocol version is supported
        if (networkProtocolVersion != CommonConstants.CURRENT_NETWORK_PROTOCOL) {
            LOGGER.mdebug("[" + connection + "] Unsupported network protocol version: " + networkProtocolVersion + " - sending error and disconnecting.");

            synchronized (connection) {
                connection.sendTCP(createResponsePacket().withError("Unsupported network protocol version").withResponseTo(message));
                connection.close();
            }
            return;
        }

        // Set the client version
        synchronized (connection) {
            connection.setClientVersion(message.getClientVersion());
        }

        LOGGER.mdebug("[" + connection + "] Requested server version");

        // Send the server version
        synchronized (connection) {
            connection.sendTCP(createResponsePacket().withResponseTo(message));
        }
    }

    /**
     * Creates a response packet
     *
     * @return Response packet
     */
    private Packets.Responses.VersionExchange createResponsePacket() {
        return new Packets.Responses.VersionExchange(CommonConstants.CURRENT_SERVER_VERSION, CommonConstants.CURRENT_NETWORK_PROTOCOL);
    }
}
