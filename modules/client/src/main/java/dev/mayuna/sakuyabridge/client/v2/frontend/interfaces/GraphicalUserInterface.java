package dev.mayuna.sakuyabridge.client.v2.frontend.interfaces;

import com.formdev.flatlaf.extras.FlatInspector;
import dev.mayuna.sakuyabridge.client.v2.frontend.FrontendConfig;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.connect.ConnectFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.main.MainFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.LanguageManager;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
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
            // dialog
            JOptionPane.showMessageDialog(null, "Failed to load language packs", "Error", JOptionPane.ERROR_MESSAGE);
        }

        //new MainFrame().openFrame();

        openConnectFrame();
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping SakuyaBridge graphical interface");

        // Save settings
        settings.save();
    }

    private void openConnectFrame() {
        var connectFrame = new ConnectFrame();
        connectFrame.openFrame();
    }
}
