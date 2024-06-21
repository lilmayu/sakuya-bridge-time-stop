package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.settings;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import dev.mayuna.sakuyabridge.client.v1.ui.utils.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.util.FlatLafThemeUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Design for the settings tabbed panel
 */
public abstract class SettingsTabbedPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTabbedPane settingsTabbedPane;
    protected JComboBox<UIManager.LookAndFeelInfo> themeComboBox;

    /**
     * Prevents themeComboBox's action listener from running when loading static data
     */
    private boolean loading = true;

    /**
     * Creates a new instance of the settings tabbed panel design
     */
    public SettingsTabbedPanelDesign() {
        super(new BorderLayout());
        loadStaticData();

        loading = false;
    }

    @Override
    protected void prepareComponents() {
        settingsTabbedPane = new JTabbedPane();

        themeComboBox = new JComboBox<>();
        themeComboBox.setToolTipText($getTranslation(Lang.Frames.Main.Panels.Settings.TOOLTIP_MAY_RESTART_SAKUYA_BRIDGE));


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
    }

    @Override
    protected void registerListeners() {
        // Selected theme combo box
        themeComboBox.addActionListener(e -> {
            if (loading) {
                return;
            }

            selectedThemeComboBox(e);
        });
    }

    protected abstract void selectedThemeComboBox(ActionEvent actionEvent);

    @Override
    protected void populatePanel() {
        this.add(settingsTabbedPane, BorderLayout.CENTER);
        prepareTabs();
    }

    /**
     * Loads static data
     */
    protected void loadStaticData() {
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

        // For some reason, just themeComboBox.setSelectedItem() doesn't work
        for (int i = 0; i < themeComboBox.getItemCount(); i++) {
            UIManager.LookAndFeelInfo lookAndFeelInfo = themeComboBox.getItemAt(i);

            if (lookAndFeelInfo.getClassName().equals(GraphicalUserInterface.INSTANCE.getSettings().getLookAndFeelClass())) {
                themeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Prepares the tabs for the tabbed panel.
     */
    private void prepareTabs() {
        settingsTabbedPane.addTab($getTranslation(Lang.Frames.Main.Panels.Settings.TAB_USER_INTERFACE_TITLE), createUserInterfaceTab());
    }

    private JPanel createUserInterfaceTab() {
        JPanel panel = new JPanel(MigLayoutUtils.createGrow());

        panel.add(new JLabel($getTranslation(Lang.Frames.Main.Panels.Settings.LABEL_THEME)), "wrap");
        panel.add(themeComboBox, "growx");

        return panel;
    }
}
