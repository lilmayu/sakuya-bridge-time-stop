package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.TabType;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.GameBrowserTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.SettingsTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowEvent;

public abstract class MainFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JTabbedPane tabbedPane;

    public MainFrameDesign() {
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.General.TEXT_TITLE));
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        CinnamonRollFlatLaf.setTabType(tabbedPane, TabType.CARD);
        CinnamonRollFlatLaf.showTabSeparator(tabbedPane, true);
        CinnamonRollFlatLaf.showBorder(tabbedPane, true);
        TabbedPanel.configureTabbedPane(tabbedPane);
    }

    @Override
    protected void registerListeners() {
    }

    private void prepareHeader() {
        JPanel headerPanel = new JPanel(MigLayoutUtils.createGrow());

        JLabel labelTitle = new JLabel($getTranslation(Lang.General.TEXT_TITLE));
        CinnamonRoll.deriveFontWith(labelTitle, Font.BOLD, 24);
        headerPanel.add(labelTitle, "gapleft 10, wrap");

        headerPanel.add(new JSeparator(), "growx");

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void prepareTabbedPane() {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        tabbedPane.addTab("Game Browser", new GameBrowserTabbedPanel());
        tabbedPane.addTab("Host Game", new JPanel());
        tabbedPane.addTab("Chat room", new JPanel());
        tabbedPane.addTab("Account", new JPanel());
        tabbedPane.addTab("Settings", new SettingsTabbedPanel());

        tabPanel.add(tabbedPane, BorderLayout.CENTER);
        this.add(tabPanel, BorderLayout.CENTER);
    }

    private void prepareFooter() {
    }
}
