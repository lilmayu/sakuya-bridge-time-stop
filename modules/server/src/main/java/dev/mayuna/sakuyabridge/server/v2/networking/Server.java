package dev.mayuna.sakuyabridge.server.v2.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.NetworkRegistration;
import dev.mayuna.sakuyabridge.server.v2.Config;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.EncryptedCommunicationVerifierListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.ServerInfoListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.ExchangeVersionListener;
import dev.mayuna.timestop.managers.EncryptionManager;
import dev.mayuna.timestop.networking.base.TimeStopServer;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.extension.CryptoKeyExchange;
import dev.mayuna.timestop.networking.extension.KeyStorage;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketCompressor;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketEncryptionTranslator;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketTranslator;
import lombok.Getter;

/**
 * Sakuya Bridge Server
 */
@Getter
public final class Server extends TimeStopServer {

    public static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Server.class);

    private final Config.Server config;
    private final KeyStorage keyStorage = new KeyStorage();
    private final EncryptionManager encryptionManager;

    /**
     * Creates a new server
     *
     * @param config Server config
     */
    public Server(Config.Server config) {
        super(config.getEndpointConfig());
        this.encryptionManager = new EncryptionManager(config.getEncryptionConfig());
        this.config = config;
    }

    /**
     * Starts the server<br>
     * Exits the application if the server fails to bind to the port
     */
    @Override
    public void start() {
        NetworkRegistration.register(getKryo());
        prepareEncryptionManager();
        registerTranslators();
        registerListener();

        super.start();

        try {
            LOGGER.info("Binding server to port " + config.getPort());
            super.bind(config.getPort());
        } catch (Exception exception) {
            LOGGER.fatal("Failed to bind server to port " + config.getPort(), exception);
            System.exit(-1);
        }
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
            System.exit(-1);
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
        translatorManager.registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt((context) -> keyStorage.getKey(context.getConnection())));
        translatorManager.registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt((context) -> keyStorage.getKey(context.getConnection())));
        translatorManager.registerTranslator(new TimeStopPacketCompressor.Compress());
        translatorManager.registerTranslator(new TimeStopPacketCompressor.Decompress());
    }

    /**
     * Registers listeners
     */
    private void registerListener() {
        LOGGER.info("Registering listeners");

        addListener(new EncryptedCommunicationVerifierListener(config.getEncryptionTimeoutMillis()));

        TimeStopListenerManager listenerManager = getListenerManager();

        listenerManager.registerListener(new CryptoKeyExchange.AsymmetricKeyListener(encryptionManager));
        listenerManager.registerListener(new CryptoKeyExchange.SymmetricKeyListener(encryptionManager, keyStorage));

        listenerManager.registerListener(new ServerInfoListener());
        listenerManager.registerListener(new ExchangeVersionListener());
    }

    @Override
    protected Connection newConnection() {
        return new SakuyaBridgeConnection(getEndpointConfig(), getListenerManager(), getTranslatorManager());
    }
}
