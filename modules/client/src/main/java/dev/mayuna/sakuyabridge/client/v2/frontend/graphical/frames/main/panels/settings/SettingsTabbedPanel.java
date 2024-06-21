package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.settings;

import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class SettingsTabbedPanel extends SettingsTabbedPanelDesign {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SettingsTabbedPanel.class);

    public SettingsTabbedPanel() {
    }

    @Override
    public void onOpen() {
        LOGGER.info("Loading settings into panel");


    }

    @Override
    public void onClose() {
    }

    @Override
    protected void selectedThemeComboBox(ActionEvent actionEvent) {
        UIManager.LookAndFeelInfo selectedTheme = (UIManager.LookAndFeelInfo) themeComboBox.getSelectedItem();

        if (selectedTheme == null) {
            return;
        }

        // Set the look and feel
        var settings = GraphicalUserInterface.INSTANCE.getSettings();
        settings.setLookAndFeelClass(selectedTheme.getClassName());
        settings.save();

        settings.applyLookAndFeel();
    }
}
