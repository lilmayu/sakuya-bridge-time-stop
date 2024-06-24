package dev.mayuna.sakuyabridge.server.v2.exceptions;

public class DuplicateGameNameException extends ServerSideException{

    @Override
    public String getErrorName() {
        return "Duplicate Game Name";
    }
}
