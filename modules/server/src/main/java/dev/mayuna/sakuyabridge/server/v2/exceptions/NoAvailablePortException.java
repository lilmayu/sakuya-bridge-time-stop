package dev.mayuna.sakuyabridge.server.v2.exceptions;

/**
 * Exception thrown when no available port is found
 */
public final class NoAvailablePortException extends ServerSideException {

    @Override
    public String getErrorName() {
        return "No available port found";
    }
}
