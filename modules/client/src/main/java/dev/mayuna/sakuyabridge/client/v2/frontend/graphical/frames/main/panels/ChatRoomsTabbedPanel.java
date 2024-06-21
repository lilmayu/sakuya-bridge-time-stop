package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatMessage;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.TimerTask;

// TODO: Minimal width for columns, cinammon roll update

/**
 * Panel for chat rooms
 */
public class ChatRoomsTabbedPanel extends TabbedPanel {

    protected java.util.Timer chatRoomUpdater = new java.util.Timer();

    protected JTabbedPane tabbedPaneChatRooms;
    protected JTextField textFieldChatBox;
    protected JButton buttonSendChatMessage;

    protected JLabel labelHeading;
    protected JTableCinnamonRoll tableCurrentlyOpened;

    protected boolean shouldFetchChatRoomsOnOpen = true;
    protected ChatRoom currentlyOpenedChatRoom;

    /**
     * Creates a new instance of the chat rooms tabbed panel
     */
    public ChatRoomsTabbedPanel() {
        super(new BorderLayout());
        this.setBorder(new EmptyBorder(0, 0, 5, 0));

        prepareComponents();
        populateData();

        updateChatRoomTabs();
        populatePanel();
    }

    /**
     * Prepares the components
     */
    private void prepareComponents() {
        labelHeading = new JLabel(LanguageManager.INSTANCE.getTranslation(Lang.General.TEXT_LOADING));
        labelHeading.setHorizontalAlignment(SwingConstants.CENTER);

        tabbedPaneChatRooms = new JTabbedPane();
        TabbedPanel.configureTabbedPane(tabbedPaneChatRooms);

        textFieldChatBox = new JTextField();
        CinnamonRoll.limitDocumentLength(textFieldChatBox, CommonConstants.MAX_MESSAGE_LENGTH);
        CinnamonRoll.onConfirm(textFieldChatBox, e -> buttonSendChatMessage.doClick());

        buttonSendChatMessage = new JButton(LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.BUTTON_SEND_MESSAGE)); // TODO: Icon
        CinnamonRoll.onClick(buttonSendChatMessage, this::clickSendChatMessage);
    }

    /**
     * Populates the data
     */
    private void populateData() {
    }

    /**
     * Populates the panel
     */
    private void populatePanel() {
        this.add(labelHeading, BorderLayout.NORTH);
        this.add(tabbedPaneChatRooms, BorderLayout.CENTER);
        this.add(createChatBoxPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the chat box panel
     *
     * @return The chat box panel
     */
    private JPanel createChatBoxPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 0, 5));

        panel.add(textFieldChatBox, BorderLayout.CENTER);
        panel.add(buttonSendChatMessage, BorderLayout.EAST);

        return panel;
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
     * Creates a table for chat messages
     *
     * @param chatRoom The chat room
     *
     * @return The table
     */
    private JTableCinnamonRoll createTableChatMessages(ChatRoom chatRoom) {
        JTableCinnamonRoll tableChatMessages = new JTableCinnamonRoll() {
            @Override
            public void paint(Graphics g) {
                this.resizeColumnWidthsToFitContent();
                super.paint(g);
            }
        };

        tableChatMessages.setTableCellRenderer(new ChatRoomTableRenderer());
        tableChatMessages.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableChatMessages.setModel(new ChatRoomTableModel(chatRoom.getChatMessages()));
        tableChatMessages.setFillsViewportHeight(true);
        tableChatMessages.setShowGrid(true);
        tableChatMessages.getTableHeader().setReorderingAllowed(false);
        tableChatMessages.getTableHeader().setResizingAllowed(true);
        tableChatMessages.setRowSelectionAllowed(false);
        tableChatMessages.setColumnSelectionAllowed(false);
        tableChatMessages.setGridColor(tableChatMessages.getBackground().darker());
        tableChatMessages.setShowGrid(false);
        tableChatMessages.setRowHeight(22);
        tableChatMessages.setAutoscrolls(false);
        tableChatMessages.setMinimumColumnWidth(70);

        return tableChatMessages;
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
                    labelHeading.setText(LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.LABEL_FAILED_TO_FETCH_CHAT_ROOMS));
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
            labelHeading.setText(LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.TEXT_NO_CHAT_ROOMS_AVAILABLE));
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
    private void clickSendChatMessage(MouseEvent mouseEvent) {
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

            TranslatedInfoMessage.create(LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.TEXT_FAILED_TO_SEND_MESSAGE)).showError(this);
        });
    }

    /**
     * Table model for the chat room
     */
    public static final class ChatRoomTableModel extends AbstractTableModel {

        public final static int TIME_COLUMN_INDEX = 0;
        public final static int USERNAME_COLUMN_INDEX = 1;
        public final static int MESSAGE_COLUMN_INDEX = 2;


        private final List<ChatMessage> chatMessages;

        /**
         * Creates a new instance of the chat room table model
         *
         * @param chatMessages The chat messages
         */
        public ChatRoomTableModel(List<ChatMessage> chatMessages) {
            this.chatMessages = chatMessages;
        }

        @Override
        public int getRowCount() {
            synchronized (chatMessages) {
                return chatMessages.size();
            }
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case TIME_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.COLUMN_TIME);
                case USERNAME_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.COLUMN_USERNAME);
                case MESSAGE_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.ChatRoomsPanel.COLUMN_MESSAGE);
                default -> LanguageManager.INSTANCE.getTranslation(Lang.General.COLUMN_UNKNOWN);
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            synchronized (chatMessages) {
                ChatMessage chatMessage = chatMessages.get(rowIndex);

                return switch (columnIndex) {
                    case TIME_COLUMN_INDEX -> chatMessage.getSentOnString();
                    case USERNAME_COLUMN_INDEX -> chatMessage.getAuthorAccount().getUsername();
                    case MESSAGE_COLUMN_INDEX -> chatMessage.getContent();
                    default -> LanguageManager.INSTANCE.getTranslation(Lang.General.COLUMN_UNKNOWN);
                };
            }
        }
    }

    /**
     * Table renderer for the chat room
     */
    public static final class ChatRoomTableRenderer extends DefaultTableCellRenderer {

        public static final Color COLOR_FOREGROUND_SYSTEM = new Color(255, 0, 0);
        public static final Color COLOR_FOREGROUND_ADMIN = new Color(255, 255, 0);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Check if the value is a string
            if (value instanceof String text) {
                // Set the color based on the level
                if (column == ChatRoomTableModel.USERNAME_COLUMN_INDEX) {
                    if (text.equals(CommonConstants.SYSTEM_USERNAME)) {
                        component.setForeground(COLOR_FOREGROUND_SYSTEM);
                    } else {
                        component.setForeground(table.getForeground());
                    }

                    // Return the component
                    return component;
                }
            }

            // Reset the color
            component.setForeground(table.getForeground());
            return component;
        }
    }
}
