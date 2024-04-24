package dev.mayuna.sakuyabridge.server.v2.networking;

import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

/**
 * A listener that will be processed only if the connection has created encrypted communication
 *
 * @param <T> The type of the message
 */
public abstract class EncryptedListener<T> extends TimeStopListener<T> {

    /**
     * Creates a new listener
     */
    public EncryptedListener(Class<T> listeningClass, int priority) {
        super(listeningClass, priority);
    }

    /**
     * Creates a new listener
     */
    public EncryptedListener(Class<T> listeningClass) {
        this(listeningClass, 0);
    }

    /**
     * Processes the message
     *
     * @param connection The connection
     * @param message    The message
     */
    public abstract void process(SakuyaBridgeConnection connection, T message);

    @Override
    public void process(@NonNull TimeStopListener.Context context, @NonNull T message) {
        if (!(context.getConnection() instanceof SakuyaBridgeConnection connection)) {
            // This should never happen
            return;
        }

        if (!connection.hasEncryptedConnection()) {
            // The connection is not encrypted; ignore.
            return;
        }

        // Process the message
        process(connection, message);
    }
}
