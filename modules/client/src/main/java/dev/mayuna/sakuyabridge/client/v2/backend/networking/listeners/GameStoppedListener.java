package dev.mayuna.sakuyabridge.client.v2.backend.networking.listeners;

import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.Packets;
import dev.mayuna.sakuyabridge.commons.v2.objects.games.GameInfo;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Listener for when a game is stopped
 */
public class GameStoppedListener extends TimeStopListener<Packets.Notifications.Game.GameStopped> {

    private final Consumer<Packets.Notifications.Game.GameStopped> onGameStopped;

    public GameStoppedListener(Consumer<Packets.Notifications.Game.GameStopped> onGameStopped) {
        super(Packets.Notifications.Game.GameStopped.class, 0);
        this.onGameStopped = onGameStopped;
    }

    @Override
    public void process(@NonNull TimeStopListener.Context context, Packets.Notifications.Game.@NonNull GameStopped gameStopped) {
        onGameStopped.accept(gameStopped);
    }
}
