package dev.mayuna.sakuyabridge.server.networking.tcp;

import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslatorManager;
import dev.mayuna.sakuyabridge.server.users.objects.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ManagedTimeStopConnection extends TimeStopConnection {

    private User user;

    /**
     * Creates a new connection with the given translator manager
     *
     * @param listenerManager listener manager
     * @param translatorManager Translator manager
     */
    public ManagedTimeStopConnection(TimeStopListenerManager listenerManager, TimeStopTranslatorManager translatorManager) {
        super(listenerManager, translatorManager);
    }
}
