package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.ResponseHelper;
import dev.mayuna.timestop.networking.timestop.TimeStopMessage;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;

/**
 * Listener specially made for authentication listeners. Checks if the authentication method is disabled before processing the message
 *
 * @param <TRequest> The type of the message
 */
abstract class AuthenticationListener<TRequest extends TimeStopPackets.BasePacket, TResponse extends TimeStopPackets.BasePacket> extends EncryptedListener<TRequest> implements ResponseHelper<TRequest, TResponse> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(AuthenticationListener.class);

    private final AuthenticationMethods authenticationMethod;
    protected boolean registerListener = false;

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
        if (registerListener && !SakuyaBridge.INSTANCE.getConfig().getServerInfo().isRegisterEnabled()) {
            LOGGER.warn("[{}] Attempted to register, but registration is disabled", connection);
            respondError(connection, request, ServerConstants.Responses.REGISTRATION_DISABLED);
            return;
        }

        if (!SakuyaBridge.INSTANCE.getConfig().getServerInfo().isAuthenticationMethodEnabled(authenticationMethod)) {
            LOGGER.warn("[{}] Attempted to login with {}, but the method is disabled", connection, authenticationMethod);
            respondError(connection, request, ServerConstants.Responses.AUTH_METHOD_DISABLED);
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
}
