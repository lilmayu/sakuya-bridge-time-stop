package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.TimerTask;

/**
 * Panel for chat rooms
 */
public final class ChatRoomsTabbedPanel extends ChatRoomsTabbedPanelDesign {

    private java.util.Timer chatRoomUpdater = new java.util.Timer();

    private boolean shouldFetchChatRoomsOnOpen = true;
    private ChatRoom currentlyOpenedChatRoom;

    /**
     * Creates a new instance of the chat rooms tabbed panel
     */
    public ChatRoomsTabbedPanel() {
        super();

        updateChatRoomTabs();
        populatePanel();
    }

    /**
     * Creates the chat messages box panel
     *
     * @return The chat messages box panel
     */
    private TabbedPanel createChatMessagesBoxPanel(ChatRoom chatRoom) {
        JTableCinnamonRoll tableChatMessages = createTableChatMessages(chatRoom);

        TabbedPanel panel = new TabbedPanel(new BorderLayout()) {
            @Override
            public void onOpen() {
                tableCurrentlyOpened = tableChatMessages;
                currentlyOpenedChatRoom = chatRoom;
            }

            @Override
            public void onClose() {
                if (tableCurrentlyOpened == tableChatMessages) {
                    tableCurrentlyOpened = null;
                }

                if (currentlyOpenedChatRoom == chatRoom) {
                    currentlyOpenedChatRoom = null;
                }
            }
        };

        var scrollPane = new JScrollPane(tableChatMessages);

        // TODO: Scrolls to the bottom, user configuration
        /*
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            scrollBar.setValue(scrollBar.getMaximum());
        });
        */

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Invoked when the tab is opened
     */
    @Override
    public void onOpen() {
        scheduleChatRoomUpdaterTask();

        if (shouldFetchChatRoomsOnOpen) {
            SakuyaBridge.INSTANCE.fetchChatRooms().thenAcceptAsync((result) -> {
                if (!result.isSuccessful()) {
                    labelHeading.setVisible(true);
                    labelHeading.setText($getTranslation(Lang.Frames.Main.Panels.ChatRooms.LABEL_FAILED_TO_FETCH_CHAT_ROOMS));
                    return;
                }

                shouldFetchChatRoomsOnOpen = false;
                updateChatRoomTabs();
            });
        }
    }

    /**
     * Invoked when the tab is closed
     */
    @Override
    public void onClose() {
        if (chatRoomUpdater != null) {
            chatRoomUpdater.cancel();
        }
    }

    /**
     * Schedules the chat room updater task
     */
    private void scheduleChatRoomUpdaterTask() {
        if (chatRoomUpdater != null) {
            chatRoomUpdater.cancel();
        }

        chatRoomUpdater = new java.util.Timer();
        chatRoomUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                updateChatRoomTabs();
                updateCurrentChatRoomMessages();
            }
        }, 0, 500);
    }


    /**
     * Prepares the tabs
     */
    public void updateChatRoomTabs() {
        var chatRooms = SakuyaBridge.INSTANCE.getActiveChatRooms();

        if (chatRooms.isEmpty()) {
            labelHeading.setVisible(true);
            labelHeading.setText($getTranslation(Lang.Frames.Main.Panels.ChatRooms.TEXT_NO_CHAT_ROOMS_AVAILABLE));
            return;
        }

        labelHeading.setVisible(false);

        SakuyaBridge.INSTANCE.getActiveChatRooms().forEach(chatRoom -> {
            String chatRoomName = chatRoom.getName();

            // Check if the tab is already opened, if so, skip
            if (tabbedPaneChatRooms.indexOfTab(chatRoomName) != -1) {
                return;
            }

            tabbedPaneChatRooms.addTab(chatRoomName, createChatMessagesBoxPanel(chatRoom));
        });
    }

    /**
     * Updates the current chat room messages
     */
    private void updateCurrentChatRoomMessages() {
        SwingUtilities.invokeLater(() -> {
            if (tableCurrentlyOpened == null || !tableCurrentlyOpened.isVisible()) {
                return;
            }

            // This updates the table's messages
            tableCurrentlyOpened.revalidate();
            tableCurrentlyOpened.repaint();
        });
    }

    /**
     * Handles the click on the send chat message button
     *
     * @param mouseEvent The mouse event
     */
    @Override
    protected void clickSendChatMessage(MouseEvent mouseEvent) {
        if (currentlyOpenedChatRoom == null) {
            // TODO: Error - no chat room selected
            return;
        }

        String content = textFieldChatBox.getText().trim();

        if (content.isBlank()) {
            return;
        }

        textFieldChatBox.setEnabled(false);
        SakuyaBridge.INSTANCE.sendChatMessage(currentlyOpenedChatRoom, content).thenAcceptAsync(result -> {
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
}
