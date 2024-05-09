package dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
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
        public void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, Packets.Requests.Auth.UsernamePasswordLogin message) {
            String username = message.getUsername();
            char[] password = message.getPassword();

            LOGGER.mdebug("[" + connection + "] Requested login as {}", username);

            Optional<UsernamePasswordAccount> authenticatedAccount;

            try {
                authenticatedAccount = SakuyaBridge.INSTANCE.getAccountManagers().getUsernamePassword().authenticate(username, password);
            } catch (Exception e) {
                LOGGER.error("[" + connection + "] Failed to authenticate as " + username + " due to an exception", e);
                connection.sendTCP(createEmptyAuthenticationResponse().withError("Internal server error").withResponseTo(message));
                return;
            }

            if (authenticatedAccount.isEmpty()) {
                LOGGER.warn("[" + connection + "] Failed to authenticate as {}", username);
                connection.sendTCP(createEmptyAuthenticationResponse().withError("Invalid credentials").withResponseTo(message));
                return;
            }

            var account = authenticatedAccount.get();
            var sessionToken = SakuyaBridge.INSTANCE.getSessionTokenManager().renewGetOrCreateSessionToken(account);

            connection.setAccount(account);
            LOGGER.mdebug("[" + connection + "] Authenticated as {}", account);
            connection.sendTCP(createEmptyAuthenticationResponse().withSessionToken(sessionToken).withResponseTo(message));
        }

        @Override
        protected Packets.Responses.Auth.UsernamePasswordLogin createEmptyAuthenticationResponse() {
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
        }

        @Override
        public void processAuthenticationMethodAllowed(SakuyaBridgeConnection connection, Packets.Requests.Auth.UsernamePasswordRegister message) {
            String username = message.getUsername();
            char[] password = message.getPassword();

            LOGGER.mdebug("[" + connection + "] Requested registration as {}", username);

            Optional<UsernamePasswordAccount> optionalAccount;

            try {
                optionalAccount = SakuyaBridge.INSTANCE.getAccountManagers().getUsernamePassword().createAccount(username, password);
            } catch (Exception e) {
                LOGGER.error("[" + connection + "] Failed to create account as " + username + " due to an exception", e);
                connection.sendTCP(createEmptyAuthenticationResponse().withError("Internal server error").withResponseTo(message));
                return;
            }

            if (optionalAccount.isEmpty()) {
                LOGGER.warn("[" + connection + "] Failed to create account as {}", username);
                connection.sendTCP(createEmptyAuthenticationResponse().withError("Username already taken").withResponseTo(message));
                return;
            }

            var account = optionalAccount.get();
            var sessionToken = SakuyaBridge.INSTANCE.getSessionTokenManager().renewGetOrCreateSessionToken(account);

            connection.setAccount(optionalAccount.get());
            LOGGER.mdebug("[" + connection + "] Created account as {}", username);
            connection.sendTCP(createEmptyAuthenticationResponse().withSessionToken(sessionToken).withResponseTo(message));
        }

        @Override
        protected Packets.Responses.Auth.UsernamePasswordRegister createEmptyAuthenticationResponse() {
            return new Packets.Responses.Auth.UsernamePasswordRegister();
        }
    }
}
