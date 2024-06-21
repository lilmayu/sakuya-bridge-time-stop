package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.sakuyabridge.client.v1.ui.utils.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.FlatLafThemeUtils;
import dev.mayuna.sakuyabridge.commons.v2.logging.SakuyaBridgeLogger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// TODO: Translations
public class SettingsTabbedPanel extends TabbedPanel {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(SettingsTabbedPanel.class);

    private JComboBox<UIManager.LookAndFeelInfo> themeComboBox;

    public SettingsTabbedPanel() {
        super(new BorderLayout());

        prepareComponents();

        prepareTabs();
    }

    @Override
    public void onOpen() {
        LOGGER.info("Loading settings into panel");

        // For some reason, just themeComboBox.setSelectedItem() doesn't work
        for (int i = 0; i < themeComboBox.getItemCount(); i++) {
            UIManager.LookAndFeelInfo lookAndFeelInfo = themeComboBox.getItemAt(i);

            if (lookAndFeelInfo.getClassName().equals(GraphicalUserInterface.INSTANCE.getSettings().getLookAndFeelClass())) {
                themeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void onClose() {
    }

    private void prepareComponents() {
        themeComboBox = new JComboBox<>();
        themeComboBox.setToolTipText("You may need to restart Sakuya Bridge for the changes to take effect");

        // Load all installed look and feels
        for (UIManager.LookAndFeelInfo installedLookAndFeel : UIManager.getInstalledLookAndFeels()) {
            themeComboBox.addItem(installedLookAndFeel);
        }

        // Load all installed FlatLaf themes
        var installedThemes = new ArrayList<>(List.of(FlatAllIJThemes.INFOS));
        installedThemes.sort(Comparator.comparing(FlatLafThemeUtils::getNameWithPrefix));
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo installedTheme : installedThemes) {
            themeComboBox.addItem(installedTheme);
        }

        // Change the renderer to show user-friendly names of themes
        themeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                UIManager.LookAndFeelInfo lookAndFeelInfo = (UIManager.LookAndFeelInfo) value;

                String name;

                if (lookAndFeelInfo instanceof FlatAllIJThemes.FlatIJLookAndFeelInfo flatIJLookAndFeelInfo) {
                    name = FlatLafThemeUtils.getNameWithPrefix(flatIJLookAndFeelInfo);
                } else {
                    name = lookAndFeelInfo.getName();
                }

                return super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
            }
        });

        // Set the selected theme
        themeComboBox.addActionListener(e -> {
            UIManager.LookAndFeelInfo selectedTheme = (UIManager.LookAndFeelInfo) themeComboBox.getSelectedItem();

            if (selectedTheme == null) {
                return;
            }

            // Set the look and feel
            var settings = GraphicalUserInterface.INSTANCE.getSettings();
            settings.setLookAndFeelClass(selectedTheme.getClassName());
            settings.save();

            settings.applyLookAndFeel();
        });
    }

    /**
     * Prepares the tabs for the tabbed panel.
     */
    private void prepareTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("User Interface", createUserInterfaceTab());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUserInterfaceTab() {
        JPanel panel = new JPanel(MigLayoutUtils.createGrow());

        panel.add(new JLabel("Theme"), "wrap");
        panel.add(themeComboBox, "growx");

        return panel;
    }
}
