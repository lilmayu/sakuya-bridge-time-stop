package dev.mayuna.sakuyabridge.server.v2.exceptions;

public abstract class ServerSideException extends RuntimeException {

    public ServerSideException() {
    }

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(Throwable cause) {
        super(cause);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the error name.
     *
     * @return The error name.
     */
    public abstract String getErrorName();

    @Override
    public String getMessage() {
        return getErrorName();
    }
}
