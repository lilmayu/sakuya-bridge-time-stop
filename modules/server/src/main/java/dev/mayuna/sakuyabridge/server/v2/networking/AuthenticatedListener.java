package dev.mayuna.sakuyabridge.server.v2.networking;

import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;

/**
 * A listener that will be processed only if the connection has authenticated
 *
 * @param <T> The type of the message
 */
public abstract class AuthenticatedListener<T> extends EncryptedListener<T> {

    /**
     * Creates a new listener
     */
    public AuthenticatedListener(Class<T> listeningClass, int priority) {
        super(listeningClass, priority);
    }

    /**
     * Creates a new listener
     */
    public AuthenticatedListener(Class<T> listeningClass) {
        this(listeningClass, 0);
    }

    /**
     * Processes the message
     *
     * @param connection The connection
     * @param user       The user
     * @param message    The message
     */
    public abstract void process(SakuyaBridgeConnection connection, User user, T message);

    /**
     * Processes the message
     *
     * @param connection The connection
     * @param message    The message
     */
    public void process(SakuyaBridgeConnection connection, T message) {
        if (!connection.isAuthenticated()) {
            // The connection is not authenticated; ignore.
            return;
        }

        // Process the message
        process(connection, connection.getUser(), message);
    }
}
