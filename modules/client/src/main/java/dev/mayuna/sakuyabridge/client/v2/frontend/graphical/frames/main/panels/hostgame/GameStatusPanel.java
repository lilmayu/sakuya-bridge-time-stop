package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.hostgame;

import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;

/**
 * game status panel
 */
public class GameStatusPanel extends GameStatusPanelDesign {

    private final Packets.Responses.Game.CreateGame createGamePacket;

    /**
     * Constructor
     *
     * @param createGamePacket The create game response
     */
    public GameStatusPanel(Packets.Responses.Game.CreateGame createGamePacket) {
        super();
        this.createGamePacket = createGamePacket;

        loadData();
    }

    /**
     * Load data
     */
    private void loadData() {
        fillData(createGamePacket.getGameInfo());
        setIp(createGamePacket.getIp());
        setPort(createGamePacket.getPort());
        setGameStatus(createGamePacket.getGameInfo().getStatus());
    }
}
