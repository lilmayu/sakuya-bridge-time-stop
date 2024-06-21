package dev.mayuna.sakuyabridge.commons.v2.objects.chat;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.ExtraRegisterArray;
import lombok.Getter;

import java.util.*;

/**
 * Represents a chat room with a name and message list
 */
@SuppressWarnings({"SynchronizeOnNonFinalField", "FieldMayBeFinal"})
@ExtraRegisterArray
public class ChatRoom {

    private @Getter String name;
    private List<ChatMessage> chatMessages = new LinkedList<>();

    /**
     * Used in serialization
     */
    public ChatRoom() {
    }

    /**
     * Creates new {@link ChatRoom}
     *
     * @param name ChatRoom's name
     */
    public ChatRoom(String name) {
        this.name = name;
    }

    /**
     * Sorts messages by their sent time
     */
    public void sortMessages() {
        synchronized (chatMessages) {
            chatMessages.sort(Comparator.comparingLong(ChatMessage::getSentOnMillisUtc));
        }
    }

    /**
     * Adds chat message to the ChatRooms message list
     *
     * @param chatMessage ChatMessage
     */
    public void addMessage(ChatMessage chatMessage) {
        sortMessages();

        synchronized (chatMessages) {
            if (chatMessages.size() == CommonConstants.MAX_MESSAGE_HISTORY_COUNT) {
                chatMessages.removeFirst(); // First message is always the oldest since the list is sorted
            }

            chatMessages.add(chatMessage);
        }
    }

    /**
     * Returns unmodifiable list of chat messages
     *
     * @return List of ChatMessages
     */
    public List<ChatMessage> getChatMessages() {
        return Collections.unmodifiableList(chatMessages);
    }
}
