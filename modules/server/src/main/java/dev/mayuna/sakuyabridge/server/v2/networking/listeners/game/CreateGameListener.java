package dev.mayuna.sakuyabridge.server.v2.networking.listeners.game;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.sakuyabridge.server.v2.managers.games.GameManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;

/**
 * Listener for {@link Packets.Requests.Game.CreateGame} packets
 */
public final class CreateGameListener extends GameListener<Packets.Requests.Game.CreateGame, Packets.Responses.Game.CreateGame> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(CreateGameListener.class);

    /**
     * Creates a new instance of {@link CreateGameListener}
     */
    public CreateGameListener() {
        super(Packets.Requests.Game.CreateGame.class);
    }

    @Override
    protected void processGamePacket(SakuyaBridgeConnection connection, GameManager gameManager, Packets.Requests.Game.CreateGame request) {
        GameInfo gameInfo = request.getGameInfo();
        String password = request.getPassword();
        String gameName = gameInfo.getName().trim();

        // Check if the game name is valid
        if (gameName.isBlank()) {
            LOGGER.warn("[{}] Tried to create a game with an empty name", connection);
            this.respondError(connection, request, "Invalid game name");
            return;
        }

        if (gameName.length() < CommonConstants.MIN_GAME_NAME_LENGTH || gameName.length() > CommonConstants.MAX_GAME_NAME_LENGTH) {
            LOGGER.warn("[{}] Tried to create a game with an invalid name length: '{}'", connection, gameName);
            this.respondError(connection, request, "Invalid game name length");
            return;
        }

        // Check if the game is password protected
        if (gameInfo.isPasswordProtected() && (password == null || password.isBlank())) {
            LOGGER.warn("[{}] Tried to create a password protected game without a password", connection);
            this.respondError(connection, request, "Password required for password protected game");
            return;
        }

        // Check if the game is compatible with the game version
        if (gameInfo.getTechnology().getCompatibleGame() != gameInfo.getVersion()) {
            LOGGER.warn("[{}] Tried to create a game with incompatible game version '{}' with '{}' (compatible: {})",
                        connection,
                        gameInfo.getVersion(),
                        gameInfo.getTechnology(),
                        gameInfo.getTechnology().getCompatibleGame()
            );

            this.respondError(connection, request, "Incompatible game version");
            return;
        }

        LOGGER.mdebug("[{}] Creating game '{}'", connection, gameName);
        gameManager.createGame(connection, gameInfo).whenCompleteAsync((game, throwable) -> {
            if (throwable != null) {
                LOGGER.error("[{}] Could not create game '{}': {}", connection, gameName, throwable.getMessage());
                this.respondError(connection, request, throwable.getMessage());
                return;
            }

            ChatRoom chatRoom = null;

            if (game.getServerChatRoomWrap() != null) {
                chatRoom = game.getServerChatRoomWrap().getChatRoom();
            }

            String ip = game.getIp();
            int port = game.getUdpServerBridge().getPort();

            this.respond(connection, request, new Packets.Responses.Game.CreateGame(gameInfo, chatRoom, ip, port));
        });
    }

    @Override
    public Packets.Responses.Game.CreateGame createResponse() {
        return new Packets.Responses.Game.CreateGame();
    }
}
