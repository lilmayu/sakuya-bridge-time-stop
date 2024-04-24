package dev.mayuna.sakuyabridge.server.v2.networking.listeners;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Listener that verifies if a connection has completed encryption in time
 */
public class EncryptedCommunicationVerifierListener implements Listener {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(EncryptedCommunicationVerifierListener.class);

    private final Timer timeoutTimer = new Timer();
    private final long timeoutMillis;

    /**
     * Creates a new listener with the given timeout
     *
     * @param timeoutMillis Timeout in milliseconds
     */
    public EncryptedCommunicationVerifierListener(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void connected(Connection connection) {
        // Start timeout timer
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkConnection(connection);
            }
        }, timeoutMillis);
    }

    /**
     * Checks if the connection is a SakuyaBridgeConnection and if it is encrypted
     *
     * @param connection Connection to check
     */
    private void checkConnection(Connection connection) {
        if (!(connection instanceof SakuyaBridgeConnection)) {
            LOGGER.warn("Connection " + connection + " is not a SakuyaBridgeConnection");
            return;
        }

        var sakuyaBridgeConnection = (SakuyaBridgeConnection) connection;

        if (sakuyaBridgeConnection.isConnected() && !sakuyaBridgeConnection.hasEncryptedConnection()) {
            LOGGER.warn("Connection " + connection + " did not complete encryption in time - closing connection");
            connection.close();
        }
    }
}
