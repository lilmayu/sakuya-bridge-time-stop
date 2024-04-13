package dev.mayuna.sakuyabridge.server.v2.networking;

import com.esotericsoftware.kryonet.FrameworkMessage;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.objects.users.User;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import dev.mayuna.timestop.networking.base.TimeStopConnection;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.base.translator.TimeStopTranslatorManager;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a connection with user and other data
 */
@Getter
@Setter
public final class SakuyaBridgeConnection extends TimeStopConnection {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridgeConnection.class);

    private final long bornTimeMillis = System.currentTimeMillis();

    private User user;

    /**
     * Creates a new connection with the given translator manager
     *
     * @param endpointConfig    Endpoint config
     * @param listenerManager   listener manager
     * @param translatorManager Translator manager
     */
    public SakuyaBridgeConnection(EndpointConfig endpointConfig, TimeStopListenerManager listenerManager, TimeStopTranslatorManager translatorManager) {
        super(endpointConfig, listenerManager, translatorManager);
    }

    /**
     * Checks if the connection is authenticated
     *
     * @return True if the connection is authenticated
     */
    public boolean isAuthenticated() {
        return user != null;
    }

    /**
     * Checks if the connection has an encrypted connection
     *
     * @return True if the connection has an encrypted connection
     */
    public boolean hasEncryptedConnection() {
        return SakuyaBridge.INSTANCE.getServer().getKeyStorage().hasKey(this);
    }

    @Override
    public int sendTCP(Object object) {
        if (!(object instanceof FrameworkMessage)) {
            LOGGER.flow("[" + this + "] Sending TCP: " + object.getClass().getSimpleName());
        }

        return super.sendTCP(object);
    }
}
