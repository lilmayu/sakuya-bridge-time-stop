package dev.mayuna.sakuyabridge.server.networking.tcp.listeners;

import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListener;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;
import lombok.NonNull;

public class EncryptedCommunicationRequestListener extends TimeStopListener<Packets.EncryptedCommunicationRequest> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(EncryptedCommunicationRequestListener.class);

    public EncryptedCommunicationRequestListener() {
        super(Packets.EncryptedCommunicationRequest.class, 100);
    }

    @Override
    public void process(@NonNull Context context, Packets.@NonNull EncryptedCommunicationRequest message) {
        TimeStopConnection connection = (TimeStopConnection) context.getConnection();

        if (connection.getPublicKey() == null) {
            LOGGER.error("Client " + connection.getRemoteAddressTCP().toString() + " requested encrypted communication but no public key is set");
            connection.sendTCP(new Packets.EncryptedCommunicationRequest().withError("No public key set"));
            connection.close();
            return;
        }

        LOGGER.info("Client " + connection.getRemoteAddressTCP().toString() + " requested encrypted communication");
        connection.sendTCP(new Packets.EncryptedCommunicationRequest());
        connection.setEncryptDataSentOverNetwork(true);
    }
}
