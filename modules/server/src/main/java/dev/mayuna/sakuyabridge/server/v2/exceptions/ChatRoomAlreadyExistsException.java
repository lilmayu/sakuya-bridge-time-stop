package dev.mayuna.sakuyabridge.server.v2.exceptions;

/**
 * Exception thrown when a chat room already exists
 */
public final class ChatRoomAlreadyExistsException extends ServerSideException {

    @Override
    public String getErrorName() {
        return "Chat Room already exists";
    }
}
