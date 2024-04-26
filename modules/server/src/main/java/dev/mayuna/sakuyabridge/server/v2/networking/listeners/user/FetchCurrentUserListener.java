package dev.mayuna.sakuyabridge.server.v2.networking.listeners.user;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.server.v2.networking.AuthenticatedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.users.StorageUserWrap;

/**
 * Listener for fetching current user
 */
public class FetchCurrentUserListener extends AuthenticatedListener<Packets.Requests.FetchCurrentUser> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FetchCurrentUserListener.class);

    /**
     * Creates a new listener
     */
    public FetchCurrentUserListener() {
        super(Packets.Requests.FetchCurrentUser.class);
    }

    @Override
    public void processAuthenticated(SakuyaBridgeConnection connection, Packets.Requests.FetchCurrentUser message) {
        LOGGER.mdebug("[{}] Requested current user", connection);

        try {
            StorageUserWrap storageUserWrap = connection.getLoadOrCreateUser();

            LOGGER.mdebug("[{}] Fetched current user: {}", connection, storageUserWrap);

            connection.sendTCP(new Packets.Responses.FetchCurrentUser(storageUserWrap.getUser()).withResponseTo(message));
        } catch (Exception exception) {
            LOGGER.error("[{}] Failed to fetch current user", connection, exception);
            connection.sendTCP(new Packets.Responses.FetchCurrentUser().withError("Internal server error").withResponseTo(message));
        }
    }
}
