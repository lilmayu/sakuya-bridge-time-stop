package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Design for the chat room panel
 */
public abstract class ChatRoomPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTextField textFieldChatBox;
    protected JButton buttonSendChatMessage;
    protected JTableCinnamonRoll tableChatMessages;

    public ChatRoomPanelDesign(ChatRoom chatRoom) {
        super(MigLayoutUtils.create());
        tableChatMessages.setModel(new ChatRoomTableModel(chatRoom.getChatMessages()));
    }

    @Override
    protected void prepareComponents() {
        textFieldChatBox = new JTextField();
        CinnamonRoll.limitDocumentLength(textFieldChatBox, CommonConstants.MAX_MESSAGE_LENGTH);

        buttonSendChatMessage = new JButton($getTranslation(Lang.Frames.Main.Panels.ChatRooms.BUTTON_SEND_MESSAGE)); // TODO: Icon

        tableChatMessages = new JTableCinnamonRoll();
        tableChatMessages.setAutoResizeColumnsOnPaint(true);
        tableChatMessages.setTableCellRenderer(new ChatRoomTableRenderer());
        tableChatMessages.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onConfirm(textFieldChatBox, this::confirmChatBox);
        CinnamonRoll.onClick(buttonSendChatMessage, this::clickSendChatMessage);
    }

    protected abstract void clickSendChatMessage(MouseEvent mouseEvent);

    protected abstract void confirmChatBox(ActionEvent event);

    @Override
    protected void populatePanel() {
        this.add(new JScrollPane(tableChatMessages), "dock center, span 2, wrap");
        this.add(textFieldChatBox, "growx");
        this.add(buttonSendChatMessage, "wrap");
    }
}
