package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms;

import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.table.JTableCinnamonRoll;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.*;

/**
 * The design of the chat rooms tabbed panel.
 */
public abstract class ChatRoomsTabbedPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTabbedPane tabbedPaneChatRooms;
    protected JLabel labelHeading;

    public ChatRoomsTabbedPanelDesign() {
        super(new BorderLayout());
    }

    @Override
    protected void prepareComponents() {
        labelHeading = new JLabel($getTranslation(Lang.General.TEXT_LOADING));
        labelHeading.setHorizontalAlignment(SwingConstants.CENTER);

        tabbedPaneChatRooms = new JTabbedPane();
        TabbedPanel.configureTabbedPane(tabbedPaneChatRooms);
    }

    @Override
    protected void registerListeners() {
    }

    /**
     * Populates the panel
     */
    @Override
    protected void populatePanel() {
        this.add(labelHeading, BorderLayout.NORTH);
        this.add(tabbedPaneChatRooms, BorderLayout.CENTER);
    }
}
