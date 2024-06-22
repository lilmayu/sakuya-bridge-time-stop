package dev.mayuna.sakuyabridge.server.v2.objects.chat;

import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;

/**
 * Implementation of server-side {@link ChatRoom}<br>
 * The list of whitelisted users is used when notifying users about new messages in a chat room
 */
public final class PublicServerChatRoomWrap extends ServerChatRoomWrap {

    /**
     * Creates new {@link PublicServerChatRoomWrap} with specified chat room
     *
     * @param chatRoom Chat room
     */
    public PublicServerChatRoomWrap(ChatRoom chatRoom) {
        super(chatRoom);

    }

    @Override
    public boolean isUserWhitelisted(User user) {
        return true;
    }

    @Override
    public boolean shouldDelete(long limitMillis) {
        return false;
    }
}
