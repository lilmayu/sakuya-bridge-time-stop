package dev.mayuna.sakuyabridge.commons.networking.tcp.timestop;

import dev.mayuna.sakuyabridge.commons.login.LoginMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * This class contains all messages that are used in the TimeStop protocol
 */
@SuppressWarnings("FieldCanBeLocal")
public class Packets {

    private Packets() {
    }

    /**
     * Error message
     */
    @Getter
    @Setter
    public static abstract class BasePacket {

        private String errorMessage;

        public BasePacket() {
        }

        /**
         * Sets the error message
         *
         * @param errorMessage The error message
         *
         * @return Itself
         */
        public BasePacket withError(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        /**
         * Checks if the packet has an error
         *
         * @return If the packet has an error
         */
        public boolean hasError() {
            return errorMessage != null;
        }
    }

    /**
     * Protocol version exchange
     */
    @Getter
    public static class ProtocolVersionExchange extends BasePacket {

        private int protocolVersion;

        public ProtocolVersionExchange() {
        }

        public ProtocolVersionExchange(int protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        /**
         * Creates a new protocol version exchange message
         *
         * @param protocolVersion The protocol version
         *
         * @return The message
         */
        public static ProtocolVersionExchange create(int protocolVersion) {
            return new ProtocolVersionExchange(protocolVersion);
        }
    }

    /**
     * Asymmetric key exchange
     */
    @Getter
    public static class AsymmetricKeyExchange extends BasePacket {

        private byte[] publicKey;

        public AsymmetricKeyExchange() {
        }

        public AsymmetricKeyExchange(byte[] publicKey) {
            this.publicKey = publicKey;
        }

        public static AsymmetricKeyExchange create(byte[] publicKey) {
            return new AsymmetricKeyExchange(publicKey);
        }
    }

    /**
     * Symmetric key exchange
     */
    @Getter
    public static class SymmetricKeyExchange extends BasePacket {

        private byte[] encryptedSymmetricKey;

        public SymmetricKeyExchange() {
        }

        public SymmetricKeyExchange(byte[] encryptedSymmetricKey) {
            this.encryptedSymmetricKey = encryptedSymmetricKey;
        }

        public static SymmetricKeyExchange create(byte[] encryptedSymmetricKey) {
            return new SymmetricKeyExchange(encryptedSymmetricKey);
        }
    }

    /**
     * Enables encrypted communication
     */
    @Getter
    public static class EncryptedCommunicationRequest extends BasePacket {

        public EncryptedCommunicationRequest() {
        }
    }

    /**
     * Login methods request
     */
    public static class LoginMethodsRequest extends BasePacket {

        public LoginMethodsRequest() {
        }
    }

    /**
     * Login methods response
     */
    public static class LoginMethodsResponse extends BasePacket {

        private LoginMethod[] loginMethods;

        public LoginMethodsResponse() {
        }

        public LoginMethodsResponse(LoginMethod[] loginMethods) {
            this.loginMethods = loginMethods;
        }

        /**
         * Returns the login methods
         *
         * @return Non-null login methods
         */
        public LoginMethod[] getLoginMethods() {
            return Objects.requireNonNullElseGet(loginMethods, () -> new LoginMethod[0]);
        }
    }
}
