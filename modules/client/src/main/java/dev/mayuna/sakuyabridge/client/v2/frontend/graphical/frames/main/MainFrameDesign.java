package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.TabType;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.account.AccountTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.chatrooms.ChatRoomsTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.GameBrowserTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.settings.SettingsTabbedPanel;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class MainFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JTabbedPane tabbedPane;
    protected JLabel labelLoggedAs;
    protected JLabel labelPing;
    protected JButton buttonDisconnect;

    public MainFrameDesign() {
        super();
        populateData();
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

    /**
     * Populates the data of the frame.
     */
    protected void populateData() {
    }

    @Override
    protected void prepareComponents() {
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        CinnamonRollFlatLaf.setTabType(tabbedPane, TabType.CARD);
        CinnamonRollFlatLaf.showTabSeparator(tabbedPane, true);
        CinnamonRollFlatLaf.showBorder(tabbedPane, true);
        TabbedPanel.configureTabbedPane(tabbedPane);

        this.labelLoggedAs = new JLabel();
        this.labelPing = new JLabel();

        this.buttonDisconnect = new JButton($getTranslation(Lang.Frames.Main.BUTTON_DISCONNECT));
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonDisconnect, this::clickDisconnect);
    }

    protected abstract void clickDisconnect(MouseEvent mouseEvent);

    private void prepareHeader() {
        JPanel headerPanel = new JPanel(MigLayoutUtils.createGrow());

        JLabel labelTitle = new JLabel($getTranslation(Lang.General.TEXT_TITLE));
        CinnamonRoll.deriveFontWith(labelTitle, Font.BOLD, 24);
        headerPanel.add(labelTitle, "gapleft 10, wrap");

        headerPanel.add(CinnamonRoll.horizontalSeparator(), "growx");

        this.add(headerPanel, BorderLayout.NORTH);
    }

    private void prepareTabbedPane() {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setBorder(new EmptyBorder(5, 10, 0, 10));

        tabbedPane.addTab($getTranslation(Lang.Frames.Main.TAB_GAME_BROWSER_TITLE), new GameBrowserTabbedPanel());
        tabbedPane.addTab($getTranslation(Lang.Frames.Main.TAB_HOST_GAME_TITLE), new JPanel());
        tabbedPane.addTab($getTranslation(Lang.Frames.Main.TAB_CHAT_ROOMS_TITLE), new ChatRoomsTabbedPanel());
        tabbedPane.addTab($getTranslation(Lang.Frames.Main.TAB_ACCOUNT_TITLE), new AccountTabbedPanel());
        tabbedPane.addTab($getTranslation(Lang.Frames.Main.TAB_SETTINGS_TITLE), new SettingsTabbedPanel());

        tabPanel.add(tabbedPane, BorderLayout.CENTER);
        this.add(tabPanel, BorderLayout.CENTER);
    }

    private void prepareFooter() {
        JPanel footerPanel = new JPanel(MigLayoutUtils.createVerbose("righttoleft"));
        footerPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        footerPanel.add(buttonDisconnect);
        footerPanel.add(CinnamonRoll.verticalSeparator(), "growy");
        footerPanel.add(new JLabel($formatTranslation(Lang.Frames.Main.LABEL_VERSION_INFO, CommonConstants.CURRENT_CLIENT_VERSION, CommonConstants.CURRENT_NETWORK_PROTOCOL)));
        footerPanel.add(CinnamonRoll.verticalSeparator(), "growy");
        footerPanel.add(labelLoggedAs);
        footerPanel.add(CinnamonRoll.verticalSeparator(), "growy");
        footerPanel.add(labelPing);

        this.add(footerPanel, BorderLayout.SOUTH);
    }
}
