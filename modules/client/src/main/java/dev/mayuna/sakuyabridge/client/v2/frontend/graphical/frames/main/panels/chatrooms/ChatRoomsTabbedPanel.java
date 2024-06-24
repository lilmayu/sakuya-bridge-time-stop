package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import java.util.TimerTask;

/**
 * Panel for chat rooms
 */
public final class ChatRoomsTabbedPanel extends ChatRoomsTabbedPanelDesign {

    // TODO: Scrolls to the bottom, user configuration
        /*
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            scrollBar.setValue(scrollBar.getMaximum());
        });
        */

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
     * Invoked when the tab is opened
     */
    @Override
    public void onOpen() {
        // Do not fetch chat rooms if chat is disabled
        if (!SakuyaBridge.INSTANCE.getServerInfo().isChatEnabled()) {
            return;
        }

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
            }
        }, 0, 1000);
    }

    /**
     * Invoked when the tab is closed
     */
    @Override
    public void onClose() {
        if (chatRoomUpdater != null) {
            chatRoomUpdater.cancel();
            chatRoomUpdater = null;
        }
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

            tabbedPaneChatRooms.addTab(chatRoomName, new ChatRoomPanel(chatRoom));
        });
    }
}
