package dev.mayuna.sakuyabridge.client.v2.backend.networking.listeners;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class ChatMessageSentListener extends TimeStopListener<Packets.Notifications.Chat.ChatMessageSent> {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create("Chat");

    /**
     * Creates a new {@link ChatMessageSentListener}
     */
    public ChatMessageSentListener() {
        super(Packets.Notifications.Chat.ChatMessageSent.class, 0);
    }

    @Override
    public void process(@NonNull Context context, Packets.Notifications.Chat.@NonNull ChatMessageSent message) {
        LOGGER.mdebug("Received chat message in Chat Room '{}': '{}'", message.getChatRoomName(), message.getChatMessage());
        SakuyaBridge.INSTANCE.receiveMessage(message.getChatRoomName(), message.getChatMessage());
    }
}
