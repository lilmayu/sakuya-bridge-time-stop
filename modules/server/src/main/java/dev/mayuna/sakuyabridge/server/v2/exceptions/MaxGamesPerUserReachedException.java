package dev.mayuna.sakuyabridge.server.v2.exceptions;

/**
 * Exception thrown when a user has reached the maximum amount of games
 */
public final class MaxGamesPerUserReachedException extends ServerSideException {

    @Override
    public String getErrorName() {
        return "Max games per user reached";
    }
}
