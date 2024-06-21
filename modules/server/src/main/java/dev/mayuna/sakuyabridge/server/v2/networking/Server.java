package dev.mayuna.sakuyabridge.server.v2.networking;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.networking.tcp.NetworkRegistration;
import dev.mayuna.sakuyabridge.commons.v2.objects.users.User;
import dev.mayuna.sakuyabridge.server.v2.config.Config;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth.SessionTokenListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.auth.UsernamePasswordListeners;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic.EncryptedCommunicationVerifierListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic.ExchangeVersionListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic.PingListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.basic.ServerInfoListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat.FetchChatRoomListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat.FetchChatRoomsListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat.LeaveChatRoomListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.chat.SendChatMessageListener;
import dev.mayuna.sakuyabridge.server.v2.networking.listeners.user.FetchCurrentUserListener;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // Library
        listenerManager.registerListener(new CryptoKeyExchange.AsymmetricKeyListener(encryptionManager));
        listenerManager.registerListener(new CryptoKeyExchange.SymmetricKeyListener(encryptionManager, keyStorage));

        // Basic
        listenerManager.registerListener(new PingListener());
        listenerManager.registerListener(new ServerInfoListener());
        listenerManager.registerListener(new ExchangeVersionListener());

        // Auth
        listenerManager.registerListener(new UsernamePasswordListeners.LoginRequestListener());
        listenerManager.registerListener(new UsernamePasswordListeners.RegisterRequestListener());
        listenerManager.registerListener(new SessionTokenListener());

        // User
        listenerManager.registerListener(new FetchCurrentUserListener());

        // Chat
        listenerManager.registerListener(new LeaveChatRoomListener());
        listenerManager.registerListener(new SendChatMessageListener());
        listenerManager.registerListener(new FetchChatRoomListener());
        listenerManager.registerListener(new FetchChatRoomsListener());
    }

    @Override
    protected Connection newConnection() {
        return new SakuyaBridgeConnection(getEndpointConfig(), getListenerManager(), getTranslatorManager());
    }

    /**
     * Gets {@link SakuyaBridgeConnection}
     *
     * @param userUuid User UUID
     *
     * @return Optional of {@link SakuyaBridgeConnection}
     */
    public Optional<SakuyaBridgeConnection> getConnectionByUserUuid(UUID userUuid) {
        return getConnections().stream()
                               .filter(connection -> ((SakuyaBridgeConnection) connection).getAccount().getUuid().equals(userUuid))
                               .findFirst()
                               .map(connection -> (SakuyaBridgeConnection) connection);
    }

    /**
     * Gets list of {@link SakuyaBridgeConnection}s filtered by the list of {@link User}s
     * @param users Users
     * @return List of {@link SakuyaBridgeConnection}s
     */
    public List<SakuyaBridgeConnection> getConnectionsByUsers(List<User> users) {
        return getConnections().stream()
                               .map(connection -> (SakuyaBridgeConnection) connection)
                               .filter(connection -> users.stream().anyMatch(user -> user.getUuid().equals(connection.getAccount().getUuid())))
                               .toList();
    }

    /**
     * Returns connections list mapped to {@link SakuyaBridgeConnection}
     * @return List of {@link SakuyaBridgeConnection}s
     */
    public List<SakuyaBridgeConnection> getConnectionsMapped() {
        return getConnections().stream().map(connection -> (SakuyaBridgeConnection) connection).toList();
    }
}
