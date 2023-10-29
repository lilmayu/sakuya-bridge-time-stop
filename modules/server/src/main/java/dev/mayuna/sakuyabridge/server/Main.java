package dev.mayuna.sakuyabridge.server;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import dev.mayuna.pumpk1n.Pumpk1n;
import dev.mayuna.pumpk1n.impl.FolderStorageHandler;
import dev.mayuna.pumpk1n.objects.DataHolder;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopServer;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketEncryptionTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;
import dev.mayuna.sakuyabridge.server.config.ServerConfigs;
import dev.mayuna.sakuyabridge.server.networking.tcp.listeners.AsymmetricKeyExchangeListener;
import dev.mayuna.sakuyabridge.server.networking.tcp.listeners.EncryptedCommunicationRequestListener;
import dev.mayuna.sakuyabridge.server.networking.tcp.listeners.ProtocolVersionExchangeListener;
import dev.mayuna.sakuyabridge.server.users.UserManagers;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);

    private static @Getter @Setter Gson gson;
    private static @Getter @Setter ServerConfigs configs;
    private static @Getter @Setter TimeStopServer server;

    private static @Getter @Setter EncryptionManager encryptionManager;
    private static @Getter @Setter Pumpk1n pumpk1n;
    private static @Getter @Setter UserManagers userManagers;

    public static void main(String[] args) {
        LOGGER.info("Sakuya Bridge: Time Stop - Server");
        LOGGER.info("========================");
        LOGGER.info("Made by: Mayuna");
        LOGGER.info("== Information ==");
        LOGGER.info("Version: " + Constants.VERSION);
        LOGGER.info("Network Protocol Version: " + NetworkConstants.COMMUNICATION_PROTOCOL_VERSION);
        LOGGER.info("=================");
        LOGGER.info("");

        long start = System.currentTimeMillis();

        prepareExitHook();
        prepareKryoLogging();
        loadGson();
        loadConfiguration();
        prepareStorage();
        prepareManagers();
        startServer();

        LOGGER.info("Started server in " + (System.currentTimeMillis() - start) + "ms");
    }

    private static void exit() {
        LOGGER.info("Stopping Sakuya Bridge...");

        if (server != null) {
            LOGGER.info("Stopping server...");
            server.stop();
        }

        if (pumpk1n != null) {
            LOGGER.info("Saving Pumpk1n's data...");
            for (DataHolder dataHolder : pumpk1n.getDataHolderList()) {
                dataHolder.save();
            }
        }

        LOGGER.info("Stopped Sakuya Bridge");
        LOGGER.success("o/");
    }

    /**
     * Prepares the exit hook
     */
    private static void prepareExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));
    }

    /**
     * Prepares Kryo logging
     */
    private static void prepareKryoLogging() {
        Log.setLogger(new KryoLogger(SakuyaBridgeLogger.create("Kryo")));
    }

    private static void loadGson() {
        LOGGER.info("Loading Gson...");

        gson = new Gson();
    }

    private static void loadConfiguration() {
        LOGGER.info("Loading configuration...");
        configs = ApplicationConfigLoader.loadFrom(gson, Constants.CONFIG_FILE_PATH, ServerConfigs.class, true);
        LOGGER.info("Loaded configuration");

        LOGGER.info("Loading encryption manager...");
        encryptionManager = new EncryptionManager(configs.getEncryptionConfig());

        // TODO: Load keys from files, if exists
        try {
            encryptionManager.generateAsymmetricKeyPair();
            encryptionManager.generateSymmetricKey();
        } catch (Exception exception) {
            LOGGER.error("Failed to generate encryption keys", exception);
            System.exit(1);
        }
    }

    private static void prepareStorage() {
        // Pumpk1n already logs
        pumpk1n = new Pumpk1n(new FolderStorageHandler(configs.getStorageConfig().getDirectoryPath()));
        pumpk1n.enableLogging(Level.INFO); // Maybe DEBUG
        pumpk1n.prepareStorage();
    }

    private static void prepareManagers() {
        LOGGER.info("Preparing managers...");
        userManagers = new UserManagers();
        userManagers.loadStoredUsers();

        // TODO: Allowed authentication methods config -> init only those, which are enabled
        userManagers.initUsernamePasswordUserManager();
    }

    private static void startServer() {
        server = new TimeStopServer(configs.getEndpointConfig());

        try {
            server.bind(configs.getServerPort());
        } catch (Exception exception) {
            LOGGER.error("Failed to bind server to port " + configs.getServerPort(), exception);
            System.exit(1);
        }

        LOGGER.info("Server was bind to listening on port " + configs.getServerPort());

        LOGGER.info("Registering server translators...");
        server.getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        server.getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        server.getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));
        server.getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));

        LOGGER.info("Registering server listeners...");
        TimeStopListenerManager listenerManager = server.getListenerManager();
        listenerManager.registerListener(new ProtocolVersionExchangeListener());
        listenerManager.registerListener(new AsymmetricKeyExchangeListener());
        listenerManager.registerListener(new EncryptedCommunicationRequestListener());

        LOGGER.info("Starting TimeStop server...");
        server.start();
    }
}
