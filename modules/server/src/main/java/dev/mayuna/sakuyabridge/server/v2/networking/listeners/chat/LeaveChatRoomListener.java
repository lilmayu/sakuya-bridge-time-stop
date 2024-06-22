package dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.ServerChatRoomWrap;

import java.util.Optional;

/**
 * {@link Packets.Requests.Chat.LeaveChatRoom} listener
 */
public final class LeaveChatRoomListener extends ChatFeatureListener<Packets.Requests.Chat.LeaveChatRoom, Packets.Responses.Chat.LeaveChatRoom> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(LeaveChatRoomListener.class);

    /**
     * Creates new {@link Packets.Requests.Chat.LeaveChatRoom} listener
     */
    public LeaveChatRoomListener() {
        super(Packets.Requests.Chat.LeaveChatRoom.class);
    }

    @Override
    public void processChatFeature(SakuyaBridgeConnection connection, ChatManager chatManager, Packets.Requests.Chat.LeaveChatRoom request) {
        Optional<ServerChatRoomWrap> optionalChatRoomWrap = chatManager.getChatRoomByName(request.getChatRoomName());

        if (optionalChatRoomWrap.isEmpty()) {
            LOGGER.warn("[{}] Tried to leave unknown Chat Room '{}'", connection, request.getChatRoomName());
            respondError(connection, request, ServerConstants.Responses.UNKNOWN_CHAT_ROOM);
            return;
        }

        ServerChatRoomWrap chatRoomWrap = optionalChatRoomWrap.get();
        User user = connection.getLoadOrCreateUser().getUser();

        if (!chatRoomWrap.isUserWhitelisted(user)) {
            LOGGER.warn("[{}] Tried to leave whitelisted Chat Room '{}'", connection, request.getChatRoomName());
            respondError(connection, request, ServerConstants.Responses.UNKNOWN_CHAT_ROOM);
            return;
        }

        chatManager.leaveChatRoom(chatRoomWrap, user);
        this.respondEmpty(connection, request);
    }

    @Override
    public Packets.Responses.Chat.LeaveChatRoom createResponse() {
        return new Packets.Responses.Chat.LeaveChatRoom();
    }
}
