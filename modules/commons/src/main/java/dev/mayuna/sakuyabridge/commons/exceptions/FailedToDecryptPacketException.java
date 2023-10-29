package dev.mayuna.sakuyabridge.commons.exceptions;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.sakuyabridge.commons.jacoco.Generated;

@Generated
public class FailedToDecryptPacketException extends RuntimeException {

    public FailedToDecryptPacketException() {
        super();
    }

    public FailedToDecryptPacketException(Connection connection) {
        super("Failed to decrypt packet from " + connection.getRemoteAddressTCP().toString());
    }
}
