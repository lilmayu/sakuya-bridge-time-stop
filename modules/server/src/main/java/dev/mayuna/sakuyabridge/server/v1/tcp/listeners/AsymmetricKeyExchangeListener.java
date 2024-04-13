package dev.mayuna.sakuyabridge.server.v1.tcp.listeners;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v1.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.base.listener.TimeStopListener;
import dev.mayuna.sakuyabridge.commons.v1.networking.tcp.timestop.Packets;
import dev.mayuna.sakuyabridge.server.v1.Main;
import lombok.NonNull;

public class AsymmetricKeyExchangeListener extends TimeStopListener<Packets.AsymmetricKeyExchange> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ProtocolVersionExchangeListener.class);

    public AsymmetricKeyExchangeListener() {
        super(Packets.AsymmetricKeyExchange.class, 100);
    }

    @Override
    public void process(@NonNull Context context, Packets.@NonNull AsymmetricKeyExchange message) {
        LOGGER.mdebug("Received asymmetric key exchange packet from " + context.getConnection()
                                                                               .getRemoteAddressTCP()
                                                                               .toString());

        TimeStopConnection connection = (TimeStopConnection) context.getConnection();

        try {
            connection.setPublicKey(EncryptionManager.loadAsymmetricPublicKeyFromBytes(message.getPublicKey()));
        } catch (Exception exception) {
            LOGGER.error("Failed to load public key that was sent by " + context.getConnection()
                                                                                .getRemoteAddressTCP()
                                                                                .toString(), exception);
            connection.sendTCP(new Packets.SymmetricKeyExchange().withError("Failed to load public key"));
            return;
        }

        byte[] encryptedSymmetricKey;

        try {
            encryptedSymmetricKey = EncryptionManager.encryptDataUsingKey(Main.getEncryptionManager().getSymmetricKeyBytes(), connection.getPublicKey());
        } catch (Exception e) {
            LOGGER.error("Failed to encrypt symmetric key", e);
            connection.sendTCP(new Packets.SymmetricKeyExchange().withError("Failed to encrypt symmetric key"));
            return;
        }

        Packets.SymmetricKeyExchange symmetricKeyExchange = new Packets.SymmetricKeyExchange(encryptedSymmetricKey);
        connection.sendTCP(symmetricKeyExchange);
    }
}
