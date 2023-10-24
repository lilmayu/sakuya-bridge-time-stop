package dev.mayuna.sakuyabridge.client;

import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.client.logging.LoggerFormLogAppender;
import dev.mayuna.sakuyabridge.client.ui.forms.connect.ConnectForm;
import dev.mayuna.sakuyabridge.commons.logging.Log4jUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopClient;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);

    private static @Getter @Setter Gson gson;
    private static @Getter @Setter ClientConfigs configs;

    private static @Getter @Setter TimeStopClient client;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

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

    private static void loadGson() {
        LOGGER.info("Loading Gson...");

        gson = new Gson();
    }

    private static void loadConfiguration() {
        LOGGER.info("Loading configuration...");
        configs = ClientConfigs.load(gson);
        LOGGER.info("Loaded configuration");
    }

    private static void prepareUserInterface() {
        LOGGER.info("Setuping FlatDarkPurpleIJTheme...");
        FlatDarkPurpleIJTheme.setup();

        Log4jUtils.addAppender(new LoggerFormLogAppender(SakuyaBridgeLogger.MDEBUG));
    }

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
        LOGGER.mdebug("Setting up TimeStopClient...");
        client = new TimeStopClient(configs.getEndpointConfig());

        client.getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        client.getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));

        LOGGER.mdebug("Starting TimeStopClient...");
        client.start();

        try {
            client.connect(10000, ip, port);
        } catch (IOException e) {
            LOGGER.error("Failed to connect to server with IP " + ip + " and port " + port, e);
            return false;
        }

        return true;
    }
}
