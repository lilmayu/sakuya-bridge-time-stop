package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

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

        LOGGER.mdebug("[" + connection + "] Requested login with previous session token");

        if (sessionTokenUuid == null) {
            LOGGER.warn("[" + connection + "] Failed to authenticate with previous session token: No session token provided");

            connection.sendTCP(createEmptyAuthenticationResponse().withError("Invalid session token").withResponseTo(message));
            return;
        }

        var sessionTokenOptional = SakuyaBridge.INSTANCE.getSessionTokenManager().getSessionTokenByTokenUuid(sessionTokenUuid);

        if (sessionTokenOptional.isEmpty()) {
            LOGGER.warn("[" + connection + "] Failed to authenticate with previous session token: Invalid/expired session token");

            connection.sendTCP(createEmptyAuthenticationResponse().withError("Invalid session token").withResponseTo(message));
            return;
        }

        var sessionToken = sessionTokenOptional.get();
        SakuyaBridge.INSTANCE.getSessionTokenManager().renewSessionToken(sessionToken);
        connection.setAccount(sessionToken.getLoggedAccount());

        LOGGER.mdebug("[" + connection + "] Authenticated with previous session token");

        connection.sendTCP(createEmptyAuthenticationResponse().withSessionToken(sessionToken).withResponseTo(message));
    }

    @Override
    protected Packets.Responses.Auth.PreviousSessionLogin createEmptyAuthenticationResponse() {
        return new Packets.Responses.Auth.PreviousSessionLogin();
    }
}
