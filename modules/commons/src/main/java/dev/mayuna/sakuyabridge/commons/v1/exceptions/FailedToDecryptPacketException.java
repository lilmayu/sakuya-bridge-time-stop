package dev.mayuna.sakuyabridge.commons.v1.exceptions;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.sakuyabridge.commons.v2.jacoco.Generated;

@Generated
public class FailedToDecryptPacketException extends RuntimeException {

    public FailedToDecryptPacketException() {
        super();
    }

    public FailedToDecryptPacketException(Connection connection) {
        super("Failed to decrypt packet from " + connection.getRemoteAddressTCP().toString());
    }
}
