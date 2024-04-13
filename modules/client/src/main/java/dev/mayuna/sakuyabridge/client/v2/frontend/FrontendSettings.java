package dev.mayuna.sakuyabridge.client.v2.frontend;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme;
import com.google.gson.Gson;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Data;

import javax.swing.*;

@Data
public final class FrontendSettings {

    private static final String SETTINGS_FILE_NAME = "graphical_frontend.json";

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(FrontendSettings.class);
    private static final Gson GSON = new Gson();

    private String lookAndFeelClass = FlatMoonlightIJTheme.class.getName(); // Default to FlatMoonlight

    /**
     * Loads the settings from the file.
     *
     * @return The settings
     */
    public static FrontendSettings load() {
        LOGGER.info("Loading frontend settings (" + SETTINGS_FILE_NAME + ")");
        return ApplicationConfigLoader.loadFrom(GSON, SETTINGS_FILE_NAME, FrontendSettings.class, false);
    }

    /**
     * Saves the settings to the file.
     */
    public void save() {
        LOGGER.info("Saving frontend settings (" + SETTINGS_FILE_NAME + ")");
        ApplicationConfigLoader.saveTo(GSON, SETTINGS_FILE_NAME, this);
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
