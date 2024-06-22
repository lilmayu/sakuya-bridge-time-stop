package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Table renderer for the chat room
 */
public final class ChatRoomTableRenderer extends DefaultTableCellRenderer {

    public static final Color COLOR_FOREGROUND_SYSTEM = new Color(255, 0, 0);
    public static final Color COLOR_FOREGROUND_LOCAL = new Color(255, 255, 0);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Check if the value is a string
        if (value instanceof String text) {
            // Set the color based on the level
            if (column == ChatRoomTableModel.USERNAME_COLUMN_INDEX) {
                if (text.equals(CommonConstants.SYSTEM_USERNAME)) {
                    component.setForeground(COLOR_FOREGROUND_SYSTEM);
                } else if (text.equals(CommonConstants.LOCAL_USERNAME)) {
                    component.setForeground(COLOR_FOREGROUND_LOCAL);

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