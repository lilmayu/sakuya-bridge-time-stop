package dev.mayuna.sakuyabridge.client.v2.frontend;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme;
import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.v2.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;
import lombok.Data;
import org.apache.logging.log4j.Level;

import javax.swing.*;

@Data
public final class FrontendConfig {

    private static final String CONFIG_FILE_NAME = "graphical-frontend-config.json";

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FrontendConfig.class);
    private static final Gson GSON = new Gson();

    private String lookAndFeelClass = FlatMoonlightIJTheme.class.getName(); // Default to FlatMoonlight
    private int minimalLogLevelLoggerFrame = Level.INFO.intLevel();
    private String lastServerAddress = "";

    /**
     * Loads the settings from the file.
     *
     * @return The settings
     */
    public static FrontendConfig load() {
        LOGGER.info("Loading frontend settings (" + CONFIG_FILE_NAME + ")");
        return ApplicationConfigLoader.loadFrom(GSON, CONFIG_FILE_NAME, FrontendConfig.class, false);
    }

    /**
     * Checks if the last server address is valid.
     *
     * @return True if the last server address is valid
     */
    public boolean isLastServerAddressValid() {
        return lastServerAddress != null && !lastServerAddress.isBlank();
    }

    /**
     * Gets the last server address.<br>
     * If the last server address is null or blank, it will return an empty string.
     *
     * @return The last server address
     */
    public String getLastServerAddress() {
        if (!isLastServerAddressValid()) {
            lastServerAddress = "";
        }

        return lastServerAddress;
    }

    /**
     * Saves the settings to the file.
     */
    public void save() {
        LOGGER.info("Saving frontend settings (" + CONFIG_FILE_NAME + ")");
        ApplicationConfigLoader.saveTo(GSON, CONFIG_FILE_NAME, this);
    }

    /**
     * Applies the settings.
     */
    public void apply() {
        LOGGER.info("Applying frontend settings");
        applyLookAndFeel();
    }

    /**
     * Applies the look and feel to the application.
     */
    public void applyLookAndFeel() {
        if (lookAndFeelClass == null) {
            LOGGER.warn("No look and feel class specified");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                FlatAnimatedLafChange.showSnapshot();
                UIManager.setLookAndFeel(lookAndFeelClass);
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
                LOGGER.info("Set look and feel: " + lookAndFeelClass);
            } catch (Exception exception) {
                LOGGER.error("Failed to set look and feel: " + lookAndFeelClass, exception);
            }
        });
    }
}
