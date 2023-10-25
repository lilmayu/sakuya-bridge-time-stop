package dev.mayuna.sakuyabridge.commons.networking.tcp.timestop;

import lombok.Getter;

/**
 * This class contains all messages that are used in the TimeStop protocol
 */
@SuppressWarnings("FieldCanBeLocal")
public class Packets {

    private Packets() {
    }

    /**
     * Protocol version exchange
     */
    @Getter
    public static class ProtocolVersionExchange {

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
    public static class AsymmetricKeyExchange {

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
    public static class SymmetricKeyExchange {

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
}
