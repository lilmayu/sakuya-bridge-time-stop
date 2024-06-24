package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.backend.networking.listeners.GameStoppedListener;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.chat.ChatRoom;

/**
 * Panel for hosting a game
 */
public final class HostGameTabbedPanel extends HostGameTabbedPanelDesign {

    public HostGameTabbedPanel() {
    }    private final GameStoppedListener gameStoppedListener = new GameStoppedListener(this::onGameStopped);

    /**
     * Invoked when the game is created
     *
     * @param createGame The create game response
     */
    @Override
    protected void onGameCreated(Packets.Responses.Game.CreateGame createGame) {
        ChatRoom chatLog;
        boolean canSendMessage;
        if (createGame.getChatRoom() != null) {
            chatLog = createGame.getChatRoom();
            canSendMessage = true;

            // Add to backend
            final var activeChats = SakuyaBridge.INSTANCE.getActiveChatRooms();
            synchronized (activeChats) {
                activeChats.add(chatLog);
            }
        } else {
            chatLog = new ChatRoom();
            canSendMessage = false;

            chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "Chatting is disabled.");
            chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "This chat window will be used only as a log.");
            chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "");
        }

        chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "Game created on the server.");
        chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "IP: " + createGame.getIp());
        chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "Port: " + createGame.getPort());

        // TODO: Enum translation
        chatLog.createMessage(CommonConstants.LOCAL_ACCOUNT, "Starting " + createGame.getGameInfo().getTechnology().name());

        changeToGameCreated(chatLog, canSendMessage);
        registerGameStoppedListener();
    }

    /**
     * Invoked when the game is stopped
     *
     * @param gameStoppedNotification The game stopped notification
     */
    private void onGameStopped(Packets.Notifications.Game.GameStopped gameStoppedNotification) {
        if (gameStoppedNotification.getReason() == Packets.Notifications.Game.GameStopped.Reason.HOST_INACTIVITY) {
            TranslatedInfoMessage.create($getTranslation(Lang.Frames.Main.Panels.HostGame.TEXT_GAME_STOPPED_HOST_INACTIVE)).showError(this);
        }

        changeToCreateGame();
    }

    @Override
    protected void changeToCreateGame() {
        super.changeToCreateGame();

        // Unregister the listener
        unregisterGameStoppedListener();
    }

    /**
     * Registers the listener for game stopped notifications
     */
    private void registerGameStoppedListener() {
        SakuyaBridge.INSTANCE.getClient().getListenerManager().registerListener(gameStoppedListener);
    }

    /**
     * Unregisters the listener for game stopped notifications
     */
    private void unregisterGameStoppedListener() {
        SakuyaBridge.INSTANCE.getClient().getListenerManager().unregisterListener(gameStoppedListener);
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }


}
