package dev.mayuna.sakuyabridge.server.v2.managers.games;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpDestination;
import dev.mayuna.sakuyabridge.commons.v2.networking.udp.UdpServerBridge;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.exceptions.CouldNotCreateUdpServerBridgeException;
import dev.mayuna.sakuyabridge.server.v2.exceptions.DuplicateGameNameException;
import dev.mayuna.sakuyabridge.server.v2.exceptions.MaxGamesPerUserReachedException;
import dev.mayuna.sakuyabridge.server.v2.exceptions.NoAvailablePortException;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.games.Game;

import java.net.SocketException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Manages games
 */
public final class GameManager {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(GameManager.class);

    private final List<Game> games = new LinkedList<>();
    private final Config.GameManager config;
    private final ChatManager chatManager;

    /**
     * Creates a new instance of GameManager
     *
     * @param config Game manager configuration
     */
    public GameManager(Config.GameManager config, ChatManager chatManager) {
        this.config = config;
        this.chatManager = chatManager;
    }

    /**
     * Initializes game manager
     */
    public void init() {
        LOGGER.success("GameManager initialized with maximum of {} starting on port {} (max {} games per user)", config.getMaxGames(), config.getStartingPort(), config.getMaxGamesPerUser());
    }

    public void registerConnectionListener() {
        LOGGER.info("Registering connection listener");
        SakuyaBridge.INSTANCE.getServer().addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                if (!(connection instanceof SakuyaBridgeConnection sakuyaBridgeConnection)) {
                    return;
                }

                // Fixes NPE when connection is not authenticated (account is null)
                if (!sakuyaBridgeConnection.isAuthenticated()) {
                    return;
                }

                var user = sakuyaBridgeConnection.getLoadOrCreateUser().getUser();
                var games = getGamesByCreatedByUserUuid(user.getUuid());

                synchronized (games) {
                    if (games.isEmpty()) {
                        return;
                    }
                }

                LOGGER.info("Connection {} closed, stopping their games", connection);

                synchronized (games) {
                    games.forEach(game -> game.getUdpServerBridge().stop());
                }
            }
        });
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
     * @throws NoAvailablePortException                No available ports
     * @throws MaxGamesPerUserReachedException         Max games per user reached
     * @throws CouldNotCreateUdpServerBridgeException} Could not create UDP Server Bridge
     */
    public synchronized CompletableFuture<Game> createGame(SakuyaBridgeConnection connection, GameInfo gameInfo) {
        if (getGameByName(gameInfo.getName()).isPresent()) {
            return CompletableFuture.failedFuture(new DuplicateGameNameException());
        }

        var optionalPort = getFirstAvailablePort();

        if (optionalPort.isEmpty()) {
            return CompletableFuture.failedFuture(new NoAvailablePortException());
        }

        int port = optionalPort.get();

        if (getGamesByCreatedByUserUuid(connection.getAccount().getUuid()).size() >= config.getMaxGamesPerUser()) {
            return CompletableFuture.failedFuture(new MaxGamesPerUserReachedException());
        }

        // Create UdpServerBridge
        UdpServerBridge udpServerBridge = new UdpServerBridge(port, config.getClientInactivityTimeoutMillis());
        udpServerBridge.getHostWhitelist().add(UdpDestination.of(connection.getLastRemoteAddressTCP()));

        // Create Game
        LOGGER.info("Creating Game for connection {} on port {} with game info {}", connection, port, gameInfo);
        gameInfo.setStatus(GameInfo.Status.STARTING);
        gameInfo.setCreatedByUser(connection.getLoadOrCreateUser().getUser());
        Game game = new Game(gameInfo);
        game.setUdpServerBridge(udpServerBridge);
        game.setIp(config.getServerIp());

        // Set server listeners
        udpServerBridge.setOnStopped(() -> serverStoppedListener(connection, game));

        // TODO: Add user's default whitelisted clients?
        //udpServerBridge.getClientWhitelist().add();

        LOGGER.mdebug("Starting UDP Server Bridge for connection {} on port {}", connection, port);
        try {
            udpServerBridge.start();
        } catch (SocketException socketException) {
            return CompletableFuture.failedFuture(new CouldNotCreateUdpServerBridgeException(socketException));
        }

        // Add game
        synchronized (games) {
            games.add(game);
        }

        LOGGER.success("Started Game for connection {} on port {}", connection, port);

        if (chatManager.isEnabled()) {
            // TODO: Broadcast game creation
            var chatRoom = chatManager.createChatRoom(game, List.of(connection.getLoadOrCreateUser().getUser()));
            game.setServerChatRoomWrap(chatRoom);
        }

        return CompletableFuture.completedFuture(game);
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

        connection.sendTCP(new Packets.Notifications.Game.GameStopped(Packets.Notifications.Game.GameStopped.Reason.HOST_INACTIVITY, game.getGameInfo()));
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
     * Returns game by name
     *
     * @param name Game name
     *
     * @return Game
     */
    public Optional<Game> getGameByName(String name) {
        synchronized (games) {
            return games.stream().filter(game -> game.getGameInfo().getName().equals(name)).findFirst();
        }
    }

    /**
     * Returns game created by user UUID
     *
     * @param userUuid User UUID
     *
     * @return Game
     */
    public List<Game> getGamesByCreatedByUserUuid(UUID userUuid) {
        synchronized (games) {
            return games.stream().filter(game -> game.getGameInfo().getCreatedByUser().getUuid().equals(userUuid)).toList();
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
