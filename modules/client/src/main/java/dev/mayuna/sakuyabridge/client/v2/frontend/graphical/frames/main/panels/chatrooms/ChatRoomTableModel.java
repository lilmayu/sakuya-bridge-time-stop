package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatMessage;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model for the chat room
 */
public final class ChatRoomTableModel extends AbstractTableModel {

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
            case TIME_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Main.Panels.ChatRooms.COLUMN_TIME);
            case USERNAME_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Main.Panels.ChatRooms.COLUMN_USERNAME);
            case MESSAGE_COLUMN_INDEX -> LanguageManager.INSTANCE.getTranslation(Lang.Frames.Main.Panels.ChatRooms.COLUMN_MESSAGE);
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
