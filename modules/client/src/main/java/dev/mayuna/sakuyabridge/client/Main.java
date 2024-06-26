package dev.mayuna.sakuyabridge.client;

import com.esotericsoftware.minlog.Log;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.logging.LoggerFormLogAppender;
import dev.mayuna.sakuyabridge.client.networking.tcp.WrappedTimeStopClient;
import dev.mayuna.sakuyabridge.client.ui.forms.connect.ConnectForm;
import dev.mayuna.sakuyabridge.commons.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.logging.Log4jUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);

    private static @Getter @Setter Gson gson;
    private static @Getter @Setter ClientConfigs configs;

    private static @Getter @Setter WrappedTimeStopClient client;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        prepareExitHook();
        loadGson();
        prepareUserInterface();
        prepareKryoLogging();

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
     * Prepares Kryo logging
     */
    private static void prepareKryoLogging() {
        Log.setLogger(new KryoLogger(SakuyaBridgeLogger.create("Kryo")));
    }

    /**
     * Opens the connect form
     */
    public static void openConnectForm() {
        ConnectForm connectForm = new ConnectForm();
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
            stopConnectionSafe();
        }

        LOGGER.mdebug("Setting up TimeStopClient...");
        client = WrappedTimeStopClient.createClient(configs);

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
     * Stops the connection safely
     */
    public static void stopConnectionSafe() {
        if (client != null) {
            LOGGER.info("Stopping client safely...");
            client.stopConnectionSafe();
            client = null;
        }
    }

    /**
     * Stops the connection forcefully
     */
    public static void stopConnectionForcefully() {
        if (client != null) {
            LOGGER.info("Stopping client forcefully...");
            client.stopConnectionForcefully();
            client = null;
        }
    }
}
