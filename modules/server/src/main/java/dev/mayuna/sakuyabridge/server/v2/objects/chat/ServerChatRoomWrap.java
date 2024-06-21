package dev.mayuna.sakuyabridge.server.v2.objects.chat;

import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of server-side {@link ChatRoom}<br>
 * The list of whitelisted users is used when notifying users about new messages in a chat room
 */
public class ServerChatRoomWrap {

    private final @Getter ChatRoom chatRoom;
    private final @Getter long createdAtMillis;
    private final List<User> whitelistedUsers = new LinkedList<>();

    /**
     * Creates new {@link ServerChatRoomWrap} with specified chat room
     *
     * @param chatRoom Chat room
     */
    public ServerChatRoomWrap(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        this.createdAtMillis = System.currentTimeMillis();
    }

    /**
     * Determines if specified User is whitelisted<br>
     * If the whitelist is empty, true is returned
     *
     * @param user User
     *
     * @return True if yes, false otherwise
     */
    public boolean isUserWhitelisted(User user) {
        synchronized (whitelistedUsers) {
            if (whitelistedUsers.isEmpty()) {
                return true;
            }

            return whitelistedUsers.contains(user);
        }
    }

    /**
     * Adds User to the whitelisted list if not already added
     *
     * @param user User
     */
    public void addUserToWhitelist(User user) {
        if (isUserWhitelisted(user)) {
            return;
        }

        synchronized (whitelistedUsers) {
            whitelistedUsers.add(user);
        }
    }

    /**
     * Adds all Users to the whitelisted list if not already added
     *
     * @param users Users
     */
    public void addAllUuidsToWhitelist(List<User> users) {
        users.forEach(this::addUserToWhitelist);
    }

    /**
     * Removes Users from the whitelisted list
     *
     * @param user User
     */
    public void removeUserFromWhitelist(User user) {
        synchronized (whitelistedUsers) {
            whitelistedUsers.remove(user);
        }
    }

    /**
     * Returns unmodifiable list of whitelisted users
     *
     * @return List of Users
     */
    public List<User> getWhitelistedUsers() {
        return Collections.unmodifiableList(whitelistedUsers);
    }

    /**
     * Determines if the Chat Room should be deleted, e.g., is existing longer than the specified limit millis
     *
     * @param limitMillis Maximum time to live for a chat room in milliseconds
     *
     * @return True if yes, false otherwise
     */
    public boolean shouldDelete(long limitMillis) {
        return System.currentTimeMillis() - createdAtMillis > limitMillis;
    }

    /**
     * Determines if the chat room is public (e.g., whitelist is empty)
     * @return True if yes, false otherwise
     */
    public boolean isPublic() {
        return whitelistedUsers.isEmpty();
    }
}
