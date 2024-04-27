package dev.mayuna.sakuyabridge.server.v2.networking;

import com.esotericsoftware.kryonet.FrameworkMessage;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.Account;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.objects.users.StorageUserWrap;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import dev.mayuna.timestop.networking.base.TimeStopConnection;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.base.translator.TimeStopTranslatorManager;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * Represents a connection with user and other data
 */
@Setter
public final class SakuyaBridgeConnection extends TimeStopConnection {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SakuyaBridgeConnection.class);

    private @Getter final long bornTimeMillis = System.currentTimeMillis();

    private @Getter InetSocketAddress lastRemoteAddressTCP;
    private @Getter int clientVersion = -1;
    private @Getter Account account;

    private StorageUserWrap storageUserWrap;

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
        return account != null;
    }

    /**
     * Checks if the connection has an encrypted connection
     *
     * @return True if the connection has an encrypted connection
     */
    public boolean hasEncryptedConnection() {
        return SakuyaBridge.INSTANCE.getServer().getKeyStorage().hasKey(this);
    }

    /**
     * Gets, loads or creates the StorageUserWrap for the connection
     *
     * @return The StorageUserWrap
     */
    public StorageUserWrap getLoadOrCreateUser() {
        if (storageUserWrap == null) {
            storageUserWrap = SakuyaBridge.INSTANCE.getUserManager().getLoadOrCreateUser(LoggedAccount.fromAccount(account));
        }

        return storageUserWrap;
    }

    @Override
    public int sendTCP(Object object) {
        if (!(object instanceof FrameworkMessage) && !(object instanceof Packets.IgnoreLogging)) {
            LOGGER.flow("[" + this + "] Sending TCP: " + object.getClass().getSimpleName());
        }

        return super.sendTCP(object);
    }

    @Override
    public String toString() {
        // mayuna[8f4e4b9e-4f7b-4f6b-8f4e-4f7b4f6b8f4e](ID){VERSION}@localhost:28748

        String remoteAddress = "[Unknown address]";

        if (getRemoteAddressTCP() != null) {
            lastRemoteAddressTCP = getRemoteAddressTCP();
        }

        if (lastRemoteAddressTCP != null) {
            remoteAddress = lastRemoteAddressTCP.toString();
        }

        return (account == null ? "Unauthenticated" : account.getUsername() + "[" + account.getUuid() + "]") + "(" + getID() + "){" + clientVersion + "}@" + remoteAddress;
    }
}
