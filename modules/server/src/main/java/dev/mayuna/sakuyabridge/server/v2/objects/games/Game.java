package dev.mayuna.sakuyabridge.server.v2.objects.games;

import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpServerBridge;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a game with a UdpServerBridge
 */
@Getter
public final class Game {

    private final GameInfo gameInfo;
    private @Setter String password;

    private @Setter UdpServerBridge udpServerBridge;

    /**
     * Creates a new instance of Game
     *
     * @param gameInfo Game info
     */
    public Game(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * Checks if the game is password protected
     *
     * @return True if the game is password protected
     */
    public boolean isPasswordProtected() {
        return password != null && gameInfo.isPasswordProtected();
    }
}
