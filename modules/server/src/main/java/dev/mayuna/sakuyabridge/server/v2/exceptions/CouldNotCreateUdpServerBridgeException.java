package dev.mayuna.sakuyabridge.server.v2.exceptions;

/**
 * Exception thrown when the server could not create a UDP server bridge
 */
public final class CouldNotCreateUdpServerBridgeException extends ServerSideException {

    public CouldNotCreateUdpServerBridgeException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getErrorName() {
        return "Could not create UDP Server Bridge";
    }
}
