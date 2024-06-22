package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Listener for the previous session token authentication method
 */
public final class SessionTokenListener extends AuthenticationListener<Packets.Requests.Auth.PreviousSessionLogin, Packets.Responses.Auth.PreviousSessionLogin> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SessionTokenListener.class);

    /**
     * Creates a new listener
     */
    public SessionTokenListener() {
        super(AuthenticationMethods.PREVIOUS_SESSION_TOKEN, Packets.Requests.Auth.PreviousSessionLogin.class);
    }

    @Override
    public void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, Packets.Requests.Auth.PreviousSessionLogin message) {
        var sessionTokenUuid = message.getPreviousSessionToken();

        LOGGER.mdebug("[{}] Requested login with previous session token", connection);

        if (sessionTokenUuid == null) {
            LOGGER.warn("[{}] Failed to authenticate with previous session token: No session token provided", connection);
            respondError(connection, message, ServerConstants.Responses.INVALID_SESSION_TOKEN);
            return;
        }

        var sessionTokenOptional = SakuyaBridge.INSTANCE.getSessionTokenManager().getSessionTokenByTokenUuid(sessionTokenUuid);

        if (sessionTokenOptional.isEmpty()) {
            LOGGER.warn("[{}] Failed to authenticate with previous session token: Invalid/expired session token", connection);
            respondError(connection, message, ServerConstants.Responses.INVALID_SESSION_TOKEN);
            return;
        }

        var sessionToken = sessionTokenOptional.get();
        SakuyaBridge.INSTANCE.getSessionTokenManager().renewSessionToken(sessionToken);
        connection.setAccount(sessionToken.getLoggedAccount());

        LOGGER.mdebug("[{}] Authenticated with previous session token", connection);

        respond(connection, message, createResponse().withSessionToken(sessionToken));
    }

    @Override
    public Packets.Responses.Auth.PreviousSessionLogin createResponse() {
        return new Packets.Responses.Auth.PreviousSessionLogin();
    }
}
