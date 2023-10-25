package dev.mayuna.sakuyabridge.server;

import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopServer;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.serialization.TimeStopSerialization;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;
import dev.mayuna.sakuyabridge.server.listeners.AsymmetricKeyExchangeListener;
import dev.mayuna.sakuyabridge.server.listeners.ProtocolVersionExchangeListener;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);

    private static @Getter @Setter Gson gson;
    private static @Getter @Setter ServerConfigs configs;
    private static @Getter @Setter TimeStopServer server;

    private static @Getter @Setter EncryptionManager encryptionManager;

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

        loadGson();
        loadConfiguration();
        startServer();

        LOGGER.info("Started server in " + (System.currentTimeMillis() - start) + "ms");
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

        LOGGER.info("Registering server listeners...");
        TimeStopListenerManager listenerManager = server.getListenerManager();
        listenerManager.registerListener(new ProtocolVersionExchangeListener());
        listenerManager.registerListener(new AsymmetricKeyExchangeListener());

        LOGGER.info("Starting TimeStop server...");
        server.start();
    }
}
