package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Panel for chat room
 */
public class ChatRoomPanel extends ChatRoomPanelDesign {

    private final ChatRoom chatRoom;
    private Timer timer;

    /**
     * Creates a new instance of the chat room panel
     *
     * @param chatRoom The chat room
     */
    public ChatRoomPanel(@NonNull ChatRoom chatRoom) {
        super(chatRoom);
        this.chatRoom = chatRoom;
    }

    @Override
    protected void clickSendChatMessage(MouseEvent mouseEvent) {
        String content = textFieldChatBox.getText().trim();

        if (content.isBlank()) {
            return;
        }

        textFieldChatBox.setEnabled(false);
        SakuyaBridge.INSTANCE.sendChatMessage(chatRoom, content).thenAcceptAsync(result -> {
            textFieldChatBox.setEnabled(true);

            if (result.isSuccessful()) {
                textFieldChatBox.setText("");
                textFieldChatBox.requestFocus();
                return;
            }

            TranslatedInfoMessage.create($getTranslation(Lang.Frames.Main.Panels.ChatRooms.TEXT_FAILED_TO_SEND_MESSAGE)).showError(this);
        });
    }

    @Override
    protected void confirmChatBox(ActionEvent event) {
        clickSendChatMessage(null);
    }

    @Override
    public void onOpen() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrentChatRoomMessages();
            }
        }, 500, 500);
    }

    /**
     * Updates the current chat room messages
     */
    private void updateCurrentChatRoomMessages() {
        SwingUtilities.invokeLater(() -> {
            // This updates the table's messages
            tableChatMessages.revalidate();
            tableChatMessages.repaint();
        });
    }

    @Override
    public void onClose() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Sets if messages can be sent
     *
     * @param canSendMessage True if messages can be sent
     */
    public void setCanSendMessage(boolean canSendMessage) {
        textFieldChatBox.setEnabled(canSendMessage);
        buttonSendChatMessage.setEnabled(canSendMessage);
    }
}
