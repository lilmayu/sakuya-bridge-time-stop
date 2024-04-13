package dev.mayuna.sakuyabridge.client.v2.backend.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import dev.mayuna.sakuyabridge.client.v2.backend.ClientConfig;
import dev.mayuna.sakuyabridge.commons.v2.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.timestop.managers.EncryptionManager;
import dev.mayuna.timestop.networking.base.TimeStopClient;
import dev.mayuna.timestop.networking.extension.CryptoKeyExchange;
import lombok.Getter;

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

        prepareEncryptionManager();
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
     * Registers the listeners
     */
    private void registerListeners() {
        LOGGER.info("Registering listeners");

        addListener(this);
    }

    @Override
    public void connected(Connection connection) {
        LOGGER.info("Connected to server");
        LOGGER.info("Encrypting the connection...");

        new CryptoKeyExchange.ClientTask(encryptionManager).runAsync(this).whenCompleteAsync((success, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to encrypt the connection", throwable);
                connection.close();
                connectionSuccessful.complete(false);
                return;
            }

            LOGGER.info("Connection encrypted");
            connectionSuccessful.complete(true);
        });
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
        if (!(object instanceof FrameworkMessage)) {
            LOGGER.flow("Sending TCP: " + object.getClass().getSimpleName());
        }

        return super.sendTCP(object);
    }
}
