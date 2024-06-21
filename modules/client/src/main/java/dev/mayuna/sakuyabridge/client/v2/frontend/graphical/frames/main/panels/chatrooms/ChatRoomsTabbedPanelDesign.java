package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * The design of the chat rooms tabbed panel.
 */
public abstract class ChatRoomsTabbedPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTabbedPane tabbedPaneChatRooms;
    protected JTextField textFieldChatBox;
    protected JButton buttonSendChatMessage;

    protected JLabel labelHeading;
    protected JTableCinnamonRoll tableCurrentlyOpened;

    public ChatRoomsTabbedPanelDesign() {
        super(new BorderLayout());
        this.setBorder(new EmptyBorder(0, 0, 5, 0));
    }

    @Override
    protected void prepareComponents() {
        labelHeading = new JLabel($getTranslation(Lang.General.TEXT_LOADING));
        labelHeading.setHorizontalAlignment(SwingConstants.CENTER);

        tabbedPaneChatRooms = new JTabbedPane();
        TabbedPanel.configureTabbedPane(tabbedPaneChatRooms);

        textFieldChatBox = new JTextField();
        CinnamonRoll.limitDocumentLength(textFieldChatBox, CommonConstants.MAX_MESSAGE_LENGTH);

        buttonSendChatMessage = new JButton($getTranslation(Lang.Frames.Main.Panels.ChatRooms.BUTTON_SEND_MESSAGE)); // TODO: Icon
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onConfirm(textFieldChatBox, this::confirmChatBox);
        CinnamonRoll.onClick(buttonSendChatMessage, this::clickSendChatMessage);
    }

    protected abstract void clickSendChatMessage(MouseEvent mouseEvent);

    protected abstract void confirmChatBox(ActionEvent event);

    /**
     * Populates the panel
     */
    @Override
    protected void populatePanel() {
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
     * Creates a table for chat messages
     *
     * @param chatRoom The chat room
     *
     * @return The table
     */
    protected JTableCinnamonRoll createTableChatMessages(ChatRoom chatRoom) {
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
}
