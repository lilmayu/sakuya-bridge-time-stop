package dev.mayuna.sakuyabridge.commons.networking.tcp.base;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.serialization.TimeStopSerialization;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslatorManager;
import lombok.Getter;

/**
 * TimeStopClient
 */
@Getter
public class TimeStopClient extends Client implements Listener {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(TimeStopClient.class);

    private final EndpointConfig endpointConfig;
    private TimeStopListenerManager listenerManager;
    private TimeStopTranslatorManager translatorManager;

    /**
     * Creates a new client with the given endpoint config
     *
     * @param endpointConfig Endpoint config
     */
    public TimeStopClient(EndpointConfig endpointConfig) {
        super(NetworkConstants.WRITE_BUFFER_SIZE, NetworkConstants.OBJECT_BUFFER_SIZE);

        this.endpointConfig = endpointConfig;

        prepare();
    }

    /**
     * Prepares the client for usage
     */
    private void prepare() {
        LOGGER.info("Preparing client...");

        // Listener & translator manager
        listenerManager = new TimeStopListenerManager(endpointConfig.getMaxThreads());
        translatorManager = new TimeStopTranslatorManager();

        // Register classes
        TimeStopSerialization.register(getKryo());

        // Register self listener
        addListener(this);
    }

    /**
     * Sends the given object to the server<br>Object will be translated before sending using {@link TimeStopTranslatorManager}.
     *
     * @param object Object to send
     *
     * @return Number of bytes sent (0 when object was translated to null)
     */
    @Override
    public int sendTCP(Object object) {
        object = translatorManager.process(new TimeStopTranslator.Context(this, TimeStopTranslator.Context.Way.OUTBOUND), object);

        if (object == null) {
            return 0;
        }

        return super.sendTCP(object);
    }

    /**
     * Sends the given object to the server<br>Object will be translated before sending using {@link TimeStopTranslatorManager}.
     *
     * @param object Object to send
     *
     * @return Number of bytes sent (0 when object was translated to null)
     */
    @Override
    public int sendUDP(Object object) {
        object = translatorManager.process(new TimeStopTranslator.Context(this, TimeStopTranslator.Context.Way.OUTBOUND), object);

        if (object == null) {
            return 0;
        }

        return super.sendUDP(object);
    }

    @Override
    public void received(Connection connection, Object object) {
        object = translatorManager.process(new TimeStopTranslator.Context(connection, TimeStopTranslator.Context.Way.INBOUND), object);

        if (object == null) {
            return;
        }

        listenerManager.process(connection, object);
    }
}
