package dev.mayuna.sakuyabridge.client.networking.tcp;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import dev.mayuna.sakuyabridge.client.ClientConfigs;
import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopClient;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketEncryptionTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class WrappedTimeStopClient extends TimeStopClient implements Listener {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(WrappedTimeStopClient.class);

    private EncryptionManager encryptionManager;

    private boolean encryptDataSentOverNetwork = false;
    private boolean ignoreClientDisconnects = false;

    /**
     * Creates a new client with the given endpoint config
     *
     * @param configs Endpoint config
     */
    private WrappedTimeStopClient(ClientConfigs configs) {
        super(configs.getEndpointConfig());
        encryptionManager = new EncryptionManager(configs.getEncryptionConfig());
    }

    /**
     * Creates a new client
     *
     * @param configs Client configs
     *
     * @return Client
     */
    public static WrappedTimeStopClient createClient(ClientConfigs configs) {
        WrappedTimeStopClient client = new WrappedTimeStopClient(configs);

        client.getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        client.getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        client.getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt(client.encryptionManager, context -> client.encryptDataSentOverNetwork));
        client.getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt(client.encryptionManager, context -> client.encryptDataSentOverNetwork));

        // Self listener
        client.addListener(client);

        return client;
    }

    /**
     * Stops the client without Connection lost message
     */
    public void stopConnectionSafe() {
        ignoreClientDisconnects = true;
        this.stop();
    }

    /**
     * Stops the client with Connection lost message
     */
    public void stopConnectionForcefully() {
        this.stop();
    }

    @Override
    public void disconnected(Connection connection) {
        LOGGER.info("Disconnected from server!");

        if (ignoreClientDisconnects) {
            return;
        }

        InfoMessages.ConnectToServer.CONNECTION_CLOSED_OR_LOST.showError();

        // Stops the client
        this.stop();

        // Closes all windows
        Window[] windows = Window.getWindows();

        for (Window window : windows) {
            window.dispose();
        }

        // Opens the connect form
        Main.openConnectForm();
    }
}
