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
    }
}
