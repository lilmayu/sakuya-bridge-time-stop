package dev.mayuna.sakuyabridge.server.v2.managers.games;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpDestination;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpServerBridge;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.exceptions.CouldNotCreateUdpServerBridgeException;
import dev.mayuna.sakuyabridge.server.v2.exceptions.MaxGamesPerUserReachedException;
import dev.mayuna.sakuyabridge.server.v2.exceptions.NoAvailablePortException;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.games.Game;

import java.net.SocketException;
import java.util.*;

/**
 * Manages games
 */
public final class GameManager {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(GameManager.class);

    private final List<Game> games = new LinkedList<>();
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
     * Initializes game manager
     */
    public void init() {
        LOGGER.success("GameManager initialized with maximum of {} starting on port {} (max {} games per user)", config.getMaxGames(), config.getStartingPort(), config.getMaxGamesPerUser());
    }

    // TODO: Packets for getting game list, game info, connect, etc. etc.

    /**
     * Creates and starts a game
     *
     * @param connection Connection
     * @param gameInfo   Game info
     *
     * @return Game
     *
     * @throws NoAvailablePortException No available ports
     * @throws MaxGamesPerUserReachedException Max games per user reached
     * @throws CouldNotCreateUdpServerBridgeException} Could not create UDP Server Bridge
     */
    public synchronized Game createGame(SakuyaBridgeConnection connection, GameInfo gameInfo) {
        // todo: CHECKING CommonConstants.MAX_GAME_NAME_LENGTH in the listener
        Optional<Integer> optionalPort = getFirstAvailablePort();

        if (optionalPort.isEmpty()) {
            throw new NoAvailablePortException();
        }

        int port = optionalPort.get();

        if (getGameByCreatedByUserUuid(connection.getAccount().getUuid()).isPresent()) {
            throw new MaxGamesPerUserReachedException();
        }

        // Create UdpServerBridge
        UdpServerBridge udpServerBridge = new UdpServerBridge(port, config.getClientInactivityTimeoutMillis());
        udpServerBridge.getHostWhitelist().add(UdpDestination.of(connection.getLastRemoteAddressTCP()));

        // Create Game
        LOGGER.mdebug("Creating Game for connection {} on port {} with game info {}", connection, port, gameInfo);
        Game game = new Game(gameInfo);
        game.setUdpServerBridge(udpServerBridge);

        // Set server listeners
        udpServerBridge.setOnStopped(() -> serverStoppedListener(connection, game));

        // TODO: Add user's default whitelisted clients?
        //udpServerBridge.getClientWhitelist().add();

        LOGGER.mdebug("Starting UDP Server Bridge for connection {} on port {}", connection, port);
        try {
            udpServerBridge.start();
        } catch (SocketException socketException) {
            throw new CouldNotCreateUdpServerBridgeException(socketException);
        }

        // Add game
        synchronized (games) {
            games.add(game);
        }

        LOGGER.success("Started Game for connection {} on port {}", connection, port);

        return game;
    }

    /**
     * Invoked when UdpServerBridge stops
     *
     * @param game Game to which the UdpServerBridge was tied to
     */
    private void serverStoppedListener(SakuyaBridgeConnection connection, Game game) {
        LOGGER.info("Game for connection {} on port {} has stopped", connection, game.getUdpServerBridge().getPort());

        synchronized (games) {
            games.remove(game);
        }
    }

    /**
     * Returns unmodifiable list of Games
     *
     * @return List of Games
     */
    private List<Game> getGames() {
        synchronized (games) {
            return Collections.unmodifiableList(games);
        }
    }

    /**
     * Returns game by port
     *
     * @param port Port
     *
     * @return Game
     */
    private Optional<Game> getGameByPort(int port) {
        synchronized (games) {
            return games.stream().filter(game -> game.getUdpServerBridge().getPort() == port).findFirst();
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
        synchronized (games) {
            return games.stream().filter(game -> game.getGameInfo().getCreatedByUser().getUuid().equals(userUuid)).findFirst();
        }
    }

    /**
     * Returns the first available port
     *
     * @return The first available port
     */
    private Optional<Integer> getFirstAvailablePort() {
        // Checks if maximum games limit was reached
        synchronized (games) {
            if (games.size() >= config.getMaxGames()) {
                return Optional.empty();
            }
        }

        int port = config.getStartingPort();

        synchronized (games) {
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
