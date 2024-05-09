package dev.mayuna.sakuyabridge.server.v2.managers.games;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.games.Game;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages games
 */
public final class GameManager {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(GameManager.class);

    private final List<Game> activeGames = new LinkedList<>();
    private final Config.GameManager config;

    /**
     * Creates a new instance of GameManager
     *
     * @param config Game manager configuration
     */
    public GameManager(Config.GameManager config) {
        this.config = config;
    }

    /**
     * Creates a game
     *
     * @param connection Connection
     * @param gameInfo   Game info
     *
     * @return Optional of Game
     */
    public Optional<Game> createGame(SakuyaBridgeConnection connection, GameInfo gameInfo) {

    }

    /**
     * Returns game by port
     *
     * @param port Port
     *
     * @return Game
     */
    private Optional<Game> getGameByPort(int port) {
        synchronized (activeGames) {
            return activeGames.stream().filter(game -> game.getUdpServerBridge().getPort() == port).findFirst();
        }
    }

    /**
     * Returns game created by user UUID
     *
     * @param userUuid User UUID
     *
     * @return Game
     */
    public Optional<Game> getGameByCreatedByUserUuid(UUID userUuid) {
        synchronized (activeGames) {
            return activeGames.stream().filter(game -> game.getGameInfo().getCreatedByUser().getUuid().equals(userUuid)).findFirst();
        }
    }

    /**
     * Returns the first available port
     *
     * @return The first available port
     */
    private Optional<Integer> getFirstAvailablePort() {
        synchronized (activeGames) {
            if (activeGames.size() >= config.getMaxGames()) {
                return Optional.empty();
            }
        }

        int port = config.getStartingPort();

        synchronized (activeGames) {
            while (getGameByPort(port).isPresent()) {
                port++;
            }
        }

        // Just in case, should not happen since the check at the start of the method
        if (port > config.getStartingPort() + config.getMaxGames()) {
            return Optional.empty();
        }

        return Optional.of(port);
    }
}
