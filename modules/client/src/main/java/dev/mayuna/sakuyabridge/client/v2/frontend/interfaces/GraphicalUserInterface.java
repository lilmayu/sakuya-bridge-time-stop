package dev.mayuna.sakuyabridge.client.v2.frontend.interfaces;

import com.formdev.flatlaf.extras.FlatInspector;
import dev.mayuna.sakuyabridge.client.v2.frontend.FrontendConfig;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.connect.ConnectFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.logger.LoggerFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.MainFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.serverinfo.ServerInfoFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.logging.LoggerFrameLogHandler;
import dev.mayuna.sakuyabridge.commons.v2.logging.Log4jUtils;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import lombok.Getter;

import javax.swing.*;

@Getter
public final class GraphicalUserInterface implements UserInterface {

    public static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create("UI");
    public static final GraphicalUserInterface INSTANCE = new GraphicalUserInterface();

    private FrontendConfig settings;

    private GraphicalUserInterface() {
    }

    @Override
    public void start() {
        LOGGER.info("Starting SakuyaBridge graphical interface");

        // FlatLaf
        FlatInspector.install("ctrl shift alt X");

        // Load settings
        settings = FrontendConfig.load();

        // Apply settings
        settings.apply();

        // Load language packs
        if (!LanguageManager.INSTANCE.loadLanguagePacks()) {
            JOptionPane.showMessageDialog(null, "Failed to load language packs", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Logger stuff
        LoggerFrameLogHandler.INSTANCE.setMinimalLogLevel(settings.getMinimalLogLevelLoggerFrame());
        Log4jUtils.addAppender(LoggerFrameLogHandler.INSTANCE);
        LoggerFrame.INSTANCE.installKeybind();

        //new MainFrame().openFrame();
        var s = new ServerInfo();
        s.getAuthenticationMethods().add(AuthenticationMethods.USERNAME_PASSWORD);
        s.getAuthenticationMethods().add(AuthenticationMethods.DISCORD);
        s.getAuthenticationMethods().add(AuthenticationMethods.ANONYMOUS);
        openServerInfo(s);

        //openConnectFrame();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping SakuyaBridge graphical interface");

        // Save settings
        settings.save();
    }

    /**
     * Opens the connect frame
     */
    public void openConnectFrame() {
        SwingUtilities.invokeLater(() -> {
            var connectFrame = new ConnectFrame();
            connectFrame.openFrame();
        });
    }

    /**
     * Opens the main frame
     *
     * @param serverInfo Server info
     */
    public void openServerInfo(ServerInfo serverInfo) {
        SwingUtilities.invokeLater(() -> {
            var serverInfoFrame = new ServerInfoFrame(serverInfo);
            serverInfoFrame.openFrame();
        });
    }

    /**
     * Opens the main frame
     */
    public void openMainFrame() {
        SwingUtilities.invokeLater(() -> {
            var mainFrame = new MainFrame();
            mainFrame.openFrame();
        });
    }
}
