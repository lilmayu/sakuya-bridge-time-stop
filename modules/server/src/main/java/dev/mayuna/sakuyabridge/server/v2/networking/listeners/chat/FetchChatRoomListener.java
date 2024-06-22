package dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.server.v2.ServerConstants;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.ServerChatRoomWrap;

import java.util.Optional;

/**
 * Listener for {@link Packets.Requests.Chat.FetchChatRoom}
 */
public final class FetchChatRoomListener extends ChatFeatureListener<Packets.Requests.Chat.FetchChatRoom, Packets.Responses.Chat.FetchChatRoom> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FetchChatRoomListener.class);

    /**
     * Creates new {@link Packets.Requests.Chat.FetchChatRoom} listener
     */
    public FetchChatRoomListener() {
        super(Packets.Requests.Chat.FetchChatRoom.class);
    }

    @Override
    public void processChatFeature(SakuyaBridgeConnection connection, ChatManager chatManager, Packets.Requests.Chat.FetchChatRoom request) {
        String chatRoomName = request.getChatRoomName();

        Optional<ServerChatRoomWrap> optionalChatRoom = chatManager.getChatRoomByName(chatRoomName);

        if (optionalChatRoom.isEmpty()) {
            LOGGER.warn("[{}] Tried to fetch unknown Chat Room '{}'", connection, chatRoomName);
            respondError(connection, request, ServerConstants.Responses.UNKNOWN_CHAT_ROOM);
            return;
        }

        LOGGER.mdebug("[{}] Fetched Chat Room '{}'", connection, chatRoomName);
        ChatRoom chatRoom = optionalChatRoom.get().getChatRoom();
        respond(connection, request, createResponse(chatRoom));
    }

    @Override
    public Packets.Responses.Chat.FetchChatRoom createResponse() {
        return new Packets.Responses.Chat.FetchChatRoom();
    }

    public Packets.Responses.Chat.FetchChatRoom createResponse(ChatRoom chatRoom) {
        return new Packets.Responses.Chat.FetchChatRoom(chatRoom);
    }
}
