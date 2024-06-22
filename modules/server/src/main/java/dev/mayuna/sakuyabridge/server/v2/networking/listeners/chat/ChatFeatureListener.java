package dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.server.v2.SakuyaBridge;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.AuthenticatedListener;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.ResponseHelper;
import dev.mayuna.timestop.networking.timestop.TimeStopPackets;

/**
 * Processes only authenticated requests when chat feature is enabled
 *
 * @param <TRequest>  The request
 * @param <TResponse> The response
 */
abstract class ChatFeatureListener<TRequest extends TimeStopPackets.BasePacket, TResponse extends TimeStopPackets.BasePacket> extends AuthenticatedListener<TRequest> implements ResponseHelper<TRequest, TResponse> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ChatFeatureListener.class);

    public ChatFeatureListener(Class<TRequest> listeningClass, int priority) {
        super(listeningClass, priority);
    }

    public ChatFeatureListener(Class<TRequest> listeningClass) {
        super(listeningClass);
    }

    @Override
    public void processAuthenticated(SakuyaBridgeConnection connection, TRequest request) {
        ChatManager chatManager = SakuyaBridge.INSTANCE.getChatManager();

        if (!chatManager.isEnabled()) {
            LOGGER.warn("[{}] Tried using Chat feature when it is disabled", connection);
            respondError(connection, request, ServerConstants.Responses.CHAT_DISABLED);
            return;
        }

        processChatFeature(connection, chatManager, request);
    }

    /**
     * Processes the request when chat feature is enabled
     *
     * @param connection  The connection
     * @param chatManager The chat manager
     * @param request     The request
     */
    public abstract void processChatFeature(SakuyaBridgeConnection connection, ChatManager chatManager, TRequest request);
}
