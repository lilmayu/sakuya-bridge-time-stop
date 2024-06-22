package dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat;

import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.server.v2.managers.chat.ChatManager;
import dev.mayuna.sakuyabridge.server.v2.networking.SakuyaBridgeConnection;
import dev.mayuna.sakuyabridge.server.v2.objects.chat.ServerChatRoomWrap;

import java.util.List;

/**
 * Listener for {@link Packets.Requests.Chat.FetchChatRooms}
 */
public final class FetchChatRoomsListener extends ChatFeatureListener<Packets.Requests.Chat.FetchChatRooms, Packets.Responses.Chat.FetchChatRooms> {

    private final static SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FetchChatRoomsListener.class);

    /**
     * Creates new {@link Packets.Requests.Chat.FetchChatRooms} listener
     */
    public FetchChatRoomsListener() {
        super(Packets.Requests.Chat.FetchChatRooms.class);
    }

    @Override
    public void processChatFeature(SakuyaBridgeConnection connection, ChatManager chatManager, Packets.Requests.Chat.FetchChatRooms request) {
        List<ServerChatRoomWrap> chatRooms = chatManager.getChatRoomsForUser(connection.getLoadOrCreateUser().getUser());

        LOGGER.mdebug("[{}] Fetched {} Chat Room(s)", connection, chatRooms.size());
        respond(connection, request, createResponse(chatRooms.stream().map(ServerChatRoomWrap::getChatRoom).toList()));
    }

    @Override
    public Packets.Responses.Chat.FetchChatRooms createResponse() {
        return new Packets.Responses.Chat.FetchChatRooms();
    }

    public Packets.Responses.Chat.FetchChatRooms createResponse(List<ChatRoom> chatRooms) {
        return new Packets.Responses.Chat.FetchChatRooms(chatRooms.toArray(ChatRoom[]::new));
    }

}
