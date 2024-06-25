package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms.ChatRoomPanel;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

import javax.swing.*;
import java.awt.*;

/**
 * Design for the game hosting panel
 */
public abstract class HostGameTabbedPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected GameCreatorPanel panelGameCreator;
    protected GameStatusPanel panelGameStatus;
    protected JLabel labelNoGameCreated;
    protected ChatRoomPanel panelChatRoom;

    public HostGameTabbedPanelDesign() {
        super(new BorderLayout());
    }

    /**
     * Sets the chat room panel
     *
     * @param chatRoom Chat room
     * @param canSendMessage True if messages can be sent
     */
    protected void changeToGameCreated(Packets.Responses.Game.CreateGame createGamePacket, ChatRoom chatRoom, boolean canSendMessage) {
        panelGameCreator.setVisible(false);
        labelNoGameCreated.setVisible(false);

        panelChatRoom = new ChatRoomPanel(chatRoom);
        panelChatRoom.setCanSendMessage(canSendMessage);
        this.add(panelChatRoom, BorderLayout.CENTER);
        panelChatRoom.onOpen();

        panelGameStatus = new GameStatusPanel(createGamePacket);
        this.add(panelGameStatus, BorderLayout.WEST);
        panelGameStatus.onOpen();
    }

    /**
     * Removes the chat room panel
     */
    protected void changeToCreateGame() {
        panelGameCreator.setVisible(true);
        labelNoGameCreated.setVisible(true);
        panelChatRoom.onClose();

        this.remove(panelChatRoom);
        this.panelChatRoom = null;
    }

    @Override
    protected void prepareComponents() {
        panelGameCreator = new GameCreatorPanel(this::onGameCreated);

        labelNoGameCreated = new JLabel("Game not created");
        labelNoGameCreated.setHorizontalAlignment(SwingConstants.CENTER);
        labelNoGameCreated.setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void registerListeners() {

    }

    protected abstract void onGameCreated(Packets.Responses.Game.CreateGame createGame);

    @Override
    protected void populatePanel() {
        this.add(panelGameCreator, BorderLayout.WEST);
        //this.add(new GameStatusPanel(new Packets.Responses.Game.CreateGame(SakuyaBridge.INSTANCE.getConfig().getLastGameInfo(), new ChatRoom(), "127.0.0.1", 27083)), BorderLayout.WEST);
        this.add(labelNoGameCreated, BorderLayout.CENTER);
    }
}
