package dev.mayuna.sakuyabridge.server.v2.networking.listeners.game;

import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.managers.games.GameManager;
import dev.mayuna.sakuyabridge.server.v2.networking.AuthenticatedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.ResponseHelper;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;

/**
 * Base listener for game listeners
 *
 * @param <TRequest>  Request packet type
 * @param <TResponse> Response packet type
 */
public abstract class GameListener<TRequest extends TimeStopPackets.BasePacket, TResponse extends TimeStopPackets.BasePacket> extends AuthenticatedListener<TRequest> implements ResponseHelper<TRequest, TResponse> {

    public GameListener(Class<TRequest> listeningClass) {
        super(listeningClass, 0);
    }

    @Override
    public void processAuthenticated(SakuyaBridgeConnection connection, TRequest request) {
        GameManager gameManager = SakuyaBridge.INSTANCE.getGameManager();
        processGamePacket(connection, gameManager, request);
    }

    /**
     * Processes a game packet
     *
     * @param connection  Connection
     * @param gameManager Game manager
     * @param request     Request
     */
    protected abstract void processGamePacket(SakuyaBridgeConnection connection, GameManager gameManager, TRequest request);
}
