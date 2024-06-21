package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.timestop.networking.timestop.TimeStopMessage;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;

/**
 * Listener specially made for authentication listeners. Checks if the authentication method is disabled before processing the message
 *
 * @param <TRequest> The type of the message
 */
abstract class AuthenticationListener<TRequest extends TimeStopMessage, TResponse extends TimeStopPackets.BasePacket> extends EncryptedListener<TRequest> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(AuthenticationListener.class);

    private final AuthenticationMethods authenticationMethod;

    /**
     * Creates a new listener
     *
     * @param authenticationMethod The authentication method for current listener
     * @param listeningClass       The class of the message
     * @param priority             The priority of the listener
     */
    public AuthenticationListener(AuthenticationMethods authenticationMethod, Class<TRequest> listeningClass, int priority) {
        super(listeningClass, priority);
        this.authenticationMethod = authenticationMethod;
    }

    /**
     * Creates a new listener
     *
     * @param authenticationMethod The authentication method for current listener
     * @param listeningClass       The class of the message
     */
    public AuthenticationListener(AuthenticationMethods authenticationMethod, Class<TRequest> listeningClass) {
        super(listeningClass);
        this.authenticationMethod = authenticationMethod;
    }

    @Override
    public void process(SakuyaBridgeConnection connection, TRequest request) {
        if (!SakuyaBridge.INSTANCE.getConfig().getServerInfo().isAuthenticationMethodEnabled(authenticationMethod)) {
            LOGGER.warn("[" + connection + "] Attempted to login with " + authenticationMethod + ", but the method is disabled");
            connection.sendTCP(createEmptyAuthenticationResponse().withError(ServerConstants.Responses.AUTH_METHOD_DISABLED).withResponseTo(request));
            return;
        }

        processAuthenticationMethodAllowed(connection, request);
    }

    /**
     * Invoked when the authentication method is allowed
     *
     * @param connection The connection
     * @param message    The message
     */
    public abstract void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, TRequest message);

    /**
     * Creates an empty authentication response for the current authentication method
     *
     * @return The response
     */
    protected abstract TResponse createEmptyAuthenticationResponse();
}
