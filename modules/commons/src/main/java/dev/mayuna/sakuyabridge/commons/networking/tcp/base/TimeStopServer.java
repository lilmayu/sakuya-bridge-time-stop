package dev.mayuna.sakuyabridge.commons.networking.tcp.base;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.serialization.TimeStopSerialization;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslatorManager;
import lombok.Getter;

/**
 * TimeStopServer
 */
@Getter
public class TimeStopServer extends Server implements Listener {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(TimeStopServer.class);

    private final EndpointConfig endpointConfig;
    private TimeStopListenerManager listenerManager;
    private TimeStopTranslatorManager translatorManager;

    /**
     * Creates a new server with the given endpoint config
     *
     * @param endpointConfig Endpoint config
     */
    public TimeStopServer(EndpointConfig endpointConfig) {
        super(NetworkConstants.WRITE_BUFFER_SIZE, NetworkConstants.OBJECT_BUFFER_SIZE);

        this.endpointConfig = endpointConfig;

        prepare();
    }

    /**
     * Prepares the server for usage
     */
    private void prepare() {
        LOGGER.info("Preparing server...");

        // Listener & translator manager
        listenerManager = new TimeStopListenerManager(endpointConfig.getMaxThreads());
        translatorManager = new TimeStopTranslatorManager();

        // Register classes
        TimeStopSerialization.register(getKryo());

        // Register self listener
        addListener(this);
    }

    /**
     * Creates a new TimeStopConnection with current translator manager
     *
     * @return Connection
     */
    @Override
    protected Connection newConnection() {
        return new TimeStopConnection(translatorManager);
    }

    /**
     * Processes received objects. Translates them using {@link TimeStopTranslatorManager} and then passes them to {@link TimeStopListenerManager}.
     *
     * @param connection Connection
     * @param object     Object
     */
    @Override
    public void received(Connection connection, Object object) {
        object = translatorManager.process(new TimeStopTranslator.Context(connection, TimeStopTranslator.Context.Way.INBOUND), object);

        if (object == null) {
            return;
        }

        listenerManager.process(connection, object);
    }
}
