package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.accounts.UsernamePasswordAccount;

import java.util.Optional;

/**
 * Listeners for the username/password authentication method
 */
public final class UsernamePasswordListeners {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create("UsernamePasswordListener");

    private UsernamePasswordListeners() {
    }

    /**
     * Listener for the login request packet
     */
    public static final class LoginRequestListener extends AuthenticationListener<Packets.Requests.Auth.UsernamePasswordLogin, Packets.Responses.Auth.UsernamePasswordLogin> {

        /**
         * Creates a new listener
         */
        public LoginRequestListener() {
            super(AuthenticationMethods.USERNAME_PASSWORD, Packets.Requests.Auth.UsernamePasswordLogin.class);
        }

        @Override
        public void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, Packets.Requests.Auth.UsernamePasswordLogin request) {
            String username = request.getUsername();
            char[] password = request.getPassword();

            LOGGER.mdebug("[{}] Requested login as {}", connection, username);

            Optional<UsernamePasswordAccount> authenticatedAccount;

            try {
                authenticatedAccount = SakuyaBridge.INSTANCE.getAccountManagers().getUsernamePassword().authenticate(username, password);
            } catch (Exception e) {
                LOGGER.error("[{}] Failed to authenticate as {} due to an exception", connection, username, e);
                respondError(connection, request, ServerConstants.Responses.INTERNAL_SERVER_ERROR);
                return;
            }

            if (authenticatedAccount.isEmpty()) {
                LOGGER.warn("[{}] Failed to authenticate as {}", connection, username);
                respondError(connection, request, ServerConstants.Responses.INVALID_CREDENTIALS);
                return;
            }

            var account = authenticatedAccount.get();
            var sessionToken = SakuyaBridge.INSTANCE.getSessionTokenManager().renewGetOrCreateSessionToken(account);

            connection.setAccount(account);
            LOGGER.mdebug("[" + connection + "] Authenticated as {}", account);
            respond(connection, request, createResponse().withSessionToken(sessionToken));
        }

        @Override
        public Packets.Responses.Auth.UsernamePasswordLogin createResponse() {
            return new Packets.Responses.Auth.UsernamePasswordLogin();
        }
    }

    /**
     * Listener for the register request packet
     */
    public static final class RegisterRequestListener extends AuthenticationListener<Packets.Requests.Auth.UsernamePasswordRegister, Packets.Responses.Auth.UsernamePasswordRegister> {

        /**
         * Creates a new listener
         */
        public RegisterRequestListener() {
            super(AuthenticationMethods.USERNAME_PASSWORD, Packets.Requests.Auth.UsernamePasswordRegister.class);
            this.registerListener = true; // Checks if registration is enabled
        }

        @Override
        public void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, Packets.Requests.Auth.UsernamePasswordRegister request) {
            String username = request.getUsername();
            char[] password = request.getPassword();

            LOGGER.mdebug("[{}] Requested registration as {}", connection, username);

            Optional<UsernamePasswordAccount> optionalAccount;

            try {
                optionalAccount = SakuyaBridge.INSTANCE.getAccountManagers().getUsernamePassword().createAccount(username, password);
            } catch (Exception e) {
                LOGGER.error("[{}] Failed to create account as {} due to an exception", connection, username, e);
                respondError(connection, request, ServerConstants.Responses.INTERNAL_SERVER_ERROR);
                return;
            }

            if (optionalAccount.isEmpty()) {
                LOGGER.warn("[{}] Failed to create account as {}", connection, username);
                respondError(connection, request, ServerConstants.Responses.USERNAME_ALREADY_TAKEN);
                return;
            }

            var account = optionalAccount.get();
            var sessionToken = SakuyaBridge.INSTANCE.getSessionTokenManager().renewGetOrCreateSessionToken(account);

            connection.setAccount(optionalAccount.get());
            LOGGER.mdebug("[{}] Created account as {}", connection, username);
            respond(connection, request, createResponse().withSessionToken(sessionToken));
        }

        @Override
        public Packets.Responses.Auth.UsernamePasswordRegister createResponse() {
            return new Packets.Responses.Auth.UsernamePasswordRegister();
        }
    }
}
