package dev.mayuna.sakuyabridge.client.v2.frontend.frames.main;

import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.TabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.main.panels.ServerBrowserTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.frames.main.panels.SettingsTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.client.v2.frontend.util.DesignUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class MainFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JTabbedPane tabbedPane;

    public MainFrameDesign() {
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.General.TITLE));
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //this.setResizable(false);
        this.setLayout(new BorderLayout());

        prepareHeader();
        prepareTabbedPane();
        prepareFooter();

        this.pack();
        this.setMinimumSize(new Dimension(500, 300));
        this.setSize(new Dimension(1000, 600));
        this.setLocationRelativeTo(parentComponent);

        this.getContentPane().requestFocusInWindow();
    }

    @Override
    protected void prepareComponents() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        DesignUtils.setTabType(tabbedPane, DesignUtils.TabType.CARD);
        DesignUtils.showTabSeparator(tabbedPane, true);
        DesignUtils.showBorder(tabbedPane, true);
        TabbedPanel.configureTabbedPane(tabbedPane);
    }

    @Override
    protected void registerListeners() {
    }

    @Override
    public void windowClosing(WindowEvent event) {
        var result = JOptionPane.showConfirmDialog(this, $getTranslation(Lang.Frames.Main.TEXT_CONFIRM_CLOSE), $getTranslation(Lang.Frames.Main.TITLE_CONFIRM_CLOSE), JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        dispose();
    }

    private void prepareHeader() {
        JPanel headerPanel = new JPanel(MigLayoutUtils.createGrow());

        JLabel labelTitle = new JLabel($getTranslation(Lang.General.TITLE));
        DesignUtils.deriveFontWith(labelTitle, Font.BOLD, 24);
        headerPanel.add(labelTitle, "gapleft 10, wrap");

        headerPanel.add(new JSeparator(), "growx");

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void prepareTabbedPane() {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        tabbedPane.addTab("Server Browser", new ServerBrowserTabbedPanel());
        tabbedPane.addTab("Host server", new JPanel());
        tabbedPane.addTab("Chat room", new JPanel());
        tabbedPane.addTab("Account", new JPanel());
        tabbedPane.addTab("Settings", new SettingsTabbedPanel());

        tabPanel.add(tabbedPane, BorderLayout.CENTER);
        this.add(tabPanel, BorderLayout.CENTER);
    }

    private void prepareFooter() {
    }
}
