package dev.mayuna.sakuyabridge.commons.exceptions;

import com.esotericsoftware.kryonet.Connection;

public class FailedToDecryptPacketException extends RuntimeException {

    public FailedToDecryptPacketException() {
        super();
    }

    public FailedToDecryptPacketException(Connection connection) {
        super("Failed to decrypt packet from " + connection.getRemoteAddressTCP().toString());
    }
}
