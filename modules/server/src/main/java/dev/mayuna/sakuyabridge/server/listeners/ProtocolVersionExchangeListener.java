package dev.mayuna.sakuyabridge.server.listeners;

import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListener;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;
import lombok.NonNull;

public class ProtocolVersionExchangeListener extends TimeStopListener<Packets.ProtocolVersionExchange> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ProtocolVersionExchangeListener.class);

    public ProtocolVersionExchangeListener() {
        super(Packets.ProtocolVersionExchange.class, 100);
    }

    @Override
    public void process(@NonNull Context context, Packets.@NonNull ProtocolVersionExchange message) {
        LOGGER.mdebug("Received protocol version exchange packet from " + context.getConnection()
                                                                                 .getRemoteAddressTCP()
                                                                                 .toString() + " with protocol version " + message.getProtocolVersion());

        int serverVersionProtocol = NetworkConstants.COMMUNICATION_PROTOCOL_VERSION;

        if (message.getProtocolVersion() != serverVersionProtocol) {
            LOGGER.warn("Protocol version mismatch! (Client: " + message.getProtocolVersion() + " Server: " + serverVersionProtocol + ") from " + context.getConnection()
                                                                                                                                                         .getRemoteAddressTCP()
                                                                                                                                                         .toString());
        }

        context.getConnection().sendTCP(new Packets.ProtocolVersionExchange(serverVersionProtocol));
    }
}
