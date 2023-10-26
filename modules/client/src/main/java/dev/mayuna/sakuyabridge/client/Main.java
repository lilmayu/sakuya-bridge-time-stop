package dev.mayuna.sakuyabridge.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.logging.LoggerFormLogAppender;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.forms.connect.ConnectForm;
import dev.mayuna.sakuyabridge.commons.logging.Log4jUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopClient;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);

    private static @Getter @Setter Gson gson;
    private static @Getter @Setter ClientConfigs configs;

    private static @Getter @Setter TimeStopClient client;
    private static @Getter @Setter EncryptionManager encryptionManager;

    private static boolean ignoreClientDisconnects = false;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        prepareExitHook();
        loadGson();
        prepareUserInterface();

        LOGGER.info("Sakuya Bridge: Time Stop - Client");
        LOGGER.info("========================");
        LOGGER.info("Made by: Mayuna");
        LOGGER.info("== Information ==");
        LOGGER.info("Version: " + Constants.VERSION);
        LOGGER.info("Network Protocol Version: " + NetworkConstants.COMMUNICATION_PROTOCOL_VERSION);
        LOGGER.info("=================");
        LOGGER.info("");
        LOGGER.warn("===== READ ME =====");
        LOGGER.warn("These logs may contain sensitive information, such as your login token, machine username, etc.");
        LOGGER.warn("Please do not share these logs with anyone unless you know what you are doing.");
        LOGGER.warn("No personal information is sent to the developers unless you have enabled Science, in which case, the machine's OS name and Java version is shared with errors.");
        LOGGER.warn("===== READ ME =====");
        LOGGER.info("");

        loadConfiguration();

        LOGGER.info("Started client in " + (System.currentTimeMillis() - start) + "ms");

        openConnectForm();
    }

    /**
     * Called when the program is exiting
     */
    private static void exit() {
        LOGGER.info("Stopping Sakuya Bridge...");

        if (client != null) {
            LOGGER.info("Stopping client...");
            client.stop();
        }

        LOGGER.info("Saving configuration...");
        configs.save(gson);

        LOGGER.info("Bye o/");
    }

    /**
     * Prepares the exit hook
     */
    private static void prepareExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(Main::exit));
    }

    /**
     * Loads Gson
     */
    private static void loadGson() {
        LOGGER.info("Loading Gson...");

        gson = new Gson();
    }

    /**
     * Loads the configuration
     */
    private static void loadConfiguration() {
        LOGGER.info("Loading configuration...");
        configs = ClientConfigs.load(gson);
        LOGGER.info("Loaded configuration");

        LOGGER.info("Loading encryption manager...");
        encryptionManager = new EncryptionManager(configs.getEncryptionConfig());
    }

    /**
     * Prepares the user interface
     */
    private static void prepareUserInterface() {
        LOGGER.info("Setuping FlatDarkPurpleIJTheme...");
        FlatDarkPurpleIJTheme.setup();

        Log4jUtils.addAppender(new LoggerFormLogAppender(SakuyaBridgeLogger.MDEBUG));
    }

    /**
     * Opens the connect form
     */
    private static void openConnectForm() {
        ConnectForm connectForm = new ConnectForm(null);
        connectForm.openForm();
    }

    /**
     * Creates a connection to the server
     *
     * @param ip   IP
     * @param port Port
     *
     * @return Whether the connection was successful
     */
    public static boolean createConnection(String ip, int port) {
        if (client != null) {
            LOGGER.warn("Client is already connected to a server! Stopping it...");
            stopConnection();
        }

        LOGGER.mdebug("Setting up TimeStopClient...");
        client = new TimeStopClient(configs.getEndpointConfig());

        client.getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        client.getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));

        // Disconnect listener
        client.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                onDisconnect();
            }
        });

        LOGGER.mdebug("Starting TimeStopClient...");
        client.start();

        try {
            client.connect(configs.getServerConnectConfig().getTimeoutMillis(), ip, port);
        } catch (IOException e) {
            LOGGER.error("Failed to connect to server with IP " + ip + " and port " + port, e);
            return false;
        }

        return true;
    }

    /**
     * Stops the connection
     */
    public static void stopConnection() {
        if (client != null) {
            LOGGER.info("Stopping client...");

            ignoreClientDisconnects = true;
            client.stop();
            ignoreClientDisconnects = false;
        }
    }

    private static void onDisconnect() {
        LOGGER.info("Disconnected from server!");

        if (ignoreClientDisconnects) {
            return;
        }

        InfoMessages.ConnectToServer.CONNECTION_LOST.showError();

        // Stops the client
        if (client != null) {
            client.stop();
            client = null;
        }

        // Closes all windows
        Window[] windows = Window.getWindows();

        for (Window window : windows) {
            window.dispose();
        }

        // Opens the connect form
        openConnectForm();
    }
}
