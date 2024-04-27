package dev.mayuna.sakuyabridge.client.v2.backend.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.client.v2.backend.ClientConfig;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.NetworkRegistration;
import dev.mayuna.sakuyabridge.commons.v2.networking.Packets;
import dev.mayuna.timestop.managers.EncryptionManager;
import dev.mayuna.timestop.networking.base.TimeStopClient;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketCompressor;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketEncryptionTranslator;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketTranslator;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;

/**
 * Sakuya Bridge client
 */
@Getter
public final class Client extends TimeStopClient implements Listener {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create("Network");

    /**
     * Future that completes when the connection is successful (is encrypted)
     */
    private final CompletableFuture<Boolean> connectionSuccessful = new CompletableFuture<>();

    private final ClientConfig config;
    private final EncryptionManager encryptionManager;

    private @Setter boolean encryptTraffic = false;
    private boolean successfullyPrepared = true;

    /**
     * Creates a new client
     *
     * @param config Client config
     */
    public Client(ClientConfig config) {
        super(config.getEndpointConfig());
        this.config = config;
        this.encryptionManager = new EncryptionManager(config.getEncryptionConfig());

        Log.setLogger(new KryoLogger(LOGGER));
    }

    @Override
    public void start() {
        LOGGER.info("Starting client");

        NetworkRegistration.register(getKryo());
        prepareEncryptionManager();
        registerTranslators();
        registerListeners();

        super.start();
    }

    /**
     * Prepares the encryption manager
     */
    private void prepareEncryptionManager() {
        LOGGER.info("Preparing Encryption Manager");

        try {
            encryptionManager.generateSymmetricKey();
            encryptionManager.generateAsymmetricKeyPair();
        } catch (Exception exception) {
            LOGGER.error("Failed to prepare encryption", exception);
            successfullyPrepared = false;
        }
    }

    /**
     * Registers the translators
     */
    private void registerTranslators() {
        LOGGER.info("Registering translators");

        var translatorManager = getTranslatorManager();

        translatorManager.registerTranslator(new TimeStopPacketTranslator());
        translatorManager.registerTranslator(new TimeStopPacketSegmentTranslator(32000));
        translatorManager.registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt((context) -> encryptTraffic ? encryptionManager.getSymmetricKey() : null));
        translatorManager.registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt((context) -> encryptTraffic ? encryptionManager.getSymmetricKey() : null));
        translatorManager.registerTranslator(new TimeStopPacketCompressor.Compress());
        translatorManager.registerTranslator(new TimeStopPacketCompressor.Decompress());
    }

    /**
     * Registers the listeners
     */
    private void registerListeners() {
        LOGGER.info("Registering listeners");

        addListener(this);
    }

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void disconnected(Connection connection) {
        LOGGER.info("Disconnected from server");

        if (!connectionSuccessful.isDone()) {
            connectionSuccessful.complete(false);
        }
    }

    @Override
    public int sendTCP(Object object) {
        if (!(object instanceof FrameworkMessage) && !(object instanceof Packets.IgnoreLogging)) {
            LOGGER.flow("Sending TCP: " + object.getClass().getSimpleName());
        }

        return super.sendTCP(object);
    }
}
