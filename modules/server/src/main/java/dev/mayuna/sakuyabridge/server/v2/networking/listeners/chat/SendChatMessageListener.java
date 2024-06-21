package dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.ServerChatRoomWrap;

import java.util.Optional;

/**
 * {@link Packets.Requests.Chat.SendChatMessage} listener
 */
public class SendChatMessageListener extends ChatFeatureListener<Packets.Requests.Chat.SendChatMessage, Packets.Responses.Chat.SendChatMessage> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SendChatMessageListener.class);

    /**
     * Creates new {@link Packets.Requests.Chat.SendChatMessage} listener
     */
    public SendChatMessageListener() {
        super(Packets.Requests.Chat.SendChatMessage.class);
    }

    @Override
    public void processChatFeature(SakuyaBridgeConnection connection, ChatManager chatManager, Packets.Requests.Chat.SendChatMessage request) {
        if (request.getContent().length() > CommonConstants.MAX_MESSAGE_LENGTH) {
            LOGGER.warn("[{}] Sent too long message", connection);
            respondError(connection, request, ServerConstants.Responses.MESSAGE_TOO_LONG);
            return;
        }

        // TODO: Chat delay

        Optional<ServerChatRoomWrap> optionalChatRoomWrap = chatManager.getChatRoomByName(request.getChatRoomName());

        if (optionalChatRoomWrap.isEmpty()) {
            LOGGER.warn("[{}] Tried to send message into unknown Chat Room '{}'", connection, request.getChatRoomName());
            respondError(connection, request, ServerConstants.Responses.UNKNOWN_CHAT_ROOM);
            return;
        }

        ServerChatRoomWrap chatRoomWrap = optionalChatRoomWrap.get();
        User user = connection.getLoadOrCreateUser().getUser();

        if (!chatRoomWrap.isUserWhitelisted(user)) {
            LOGGER.warn("[{}] Tried to send message into whitelisted Chat Room '{}'", connection, request.getChatRoomName());
            respondError(connection, request, ServerConstants.Responses.UNKNOWN_CHAT_ROOM);
            return;
        }

        chatManager.sendMessage(chatRoomWrap, chatManager.createChatMessage(user.getLoggedAccount(), request.getContent()));
        respondSuccess(connection, request);
    }

    @Override
    public Packets.Responses.Chat.SendChatMessage createResponse() {
        return new Packets.Responses.Chat.SendChatMessage();
    }
}
