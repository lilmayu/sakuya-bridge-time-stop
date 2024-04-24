package dev.mayuna.sakuyabridge.server.v2.networking.listeners;

import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.sakuyabridge.server.v2.networking.EncryptedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Listeners for the username/password authentication method
 */
public final class UsernamePasswordListeners {

    private UsernamePasswordListeners() {
    }

    /**
     * Listener for the login request packet
     */
    public static final class LoginRequestListener extends EncryptedListener<Packets.Requests.PreviousSessionLogin> {

        /**
         * Creates a new listener
         */
        public LoginRequestListener() {
            super(Packets.Requests.PreviousSessionLogin.class);
        }

        @Override
        public void process(SakuyaBridgeConnection connection, Packets.Requests.PreviousSessionLogin message) {

        }
    }

    /**
     * Listener for the register request packet
     */
    public static final class RegisterRequestListener extends EncryptedListener<Packets.Requests.UsernamePasswordRegister> {

        /**
         * Creates a new listener
         */
        public RegisterRequestListener() {
            super(Packets.Requests.UsernamePasswordRegister.class);
        }

        @Override
        public void process(SakuyaBridgeConnection connection, Packets.Requests.UsernamePasswordRegister message) {

        }
    }
}
