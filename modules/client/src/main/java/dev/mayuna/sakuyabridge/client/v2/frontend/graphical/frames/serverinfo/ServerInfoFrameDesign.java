package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.serverinfo;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import dev.mayuna.sakuyabridge.commons.v2.objects.auth.AuthenticationMethods;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

public abstract class ServerInfoFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected ServerInfo serverInfo;

    protected JTextField textFieldServerName;
    protected JTextField textFieldServerMaintainer;
    protected JTextField textFieldServerRegion;
    protected JTextArea textAreaServerMotd;

    protected JButton buttonContinueInPreviousSession;
    protected JButton buttonAuthWithDiscord;
    protected JButton buttonAuthWithUsernamePassword;
    protected JButton buttonAuthAnonymously;
    protected JButton buttonDisconnect;

    public ServerInfoFrameDesign(ServerInfo serverInfo) {
        super();

        this.serverInfo = serverInfo;

        populateData();
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.Frames.ServerInfo.TEXT_TITLE));
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareHeader();
        prepareBody();
        prepareAuthenticationMethods();

        this.pack();
        this.setLocationRelativeTo(parentComponent);

        this.getContentPane().requestFocusInWindow();
    }

    @Override
    protected void prepareComponents() {
        // TODO: Implementovat previous session
        buttonContinueInPreviousSession = new JButton($getTranslation(Lang.Frames.ServerInfo.BUTTON_CONTINUE_IN_PREVIOUS_SESSION_NO_SESSION));
        buttonAuthWithDiscord = new JButton($getTranslation(Lang.Frames.ServerInfo.BUTTON_AUTH_DISCORD));
        buttonAuthWithUsernamePassword = new JButton($getTranslation(Lang.Frames.ServerInfo.BUTTON_AUTH_USERNAME_PASSWORD));
        buttonAuthAnonymously = new JButton($getTranslation(Lang.Frames.ServerInfo.BUTTON_AUTH_ANONYMOUS));
        buttonDisconnect = new JButton($getTranslation(Lang.Frames.ServerInfo.BUTTON_DISCONNECT));

        buttonContinueInPreviousSession.setEnabled(false);

        buttonAuthWithDiscord.setMinimumSize(new Dimension(0, 30));
        buttonAuthWithUsernamePassword.setMinimumSize(new Dimension(0, 30));
        buttonAuthAnonymously.setMinimumSize(new Dimension(0, 30));

        textFieldServerName = new JTextField();
        textFieldServerMaintainer = new JTextField();
        textFieldServerRegion = new JTextField();
        textAreaServerMotd = new JTextArea();


        textFieldServerName.setEditable(false);
        textFieldServerMaintainer.setEditable(false);
        textFieldServerRegion.setEditable(false);

        textAreaServerMotd.setEditable(false);
        textAreaServerMotd.setLineWrap(true);

        buttonContinueInPreviousSession.setToolTipText($getTranslation(Lang.Frames.ServerInfo.TOOLTIP_CONTINUE_IN_PREVIOUS_SESSION));
        buttonAuthWithDiscord.setToolTipText($getTranslation(Lang.Frames.ServerInfo.TOOLTIP_AUTH_DISCORD));
        buttonAuthWithUsernamePassword.setToolTipText($getTranslation(Lang.Frames.ServerInfo.TOOLTIP_AUTH_USERNAME_PASSWORD));
        buttonAuthAnonymously.setToolTipText($getTranslation(Lang.Frames.ServerInfo.TOOLTIP_AUTH_ANONYMOUS));
    }

    /**
     * Populates the data from the server info object to the text fields, and enables/disables the buttons based on the authentication methods.
     */
    protected void populateData() {
        textFieldServerName.setText(serverInfo.getName());
        textFieldServerMaintainer.setText(serverInfo.getMaintainer());
        textFieldServerRegion.setText(serverInfo.getRegion());
        textAreaServerMotd.setText(serverInfo.getMotd());

        buttonAuthWithDiscord.setEnabled(false);
        buttonAuthWithUsernamePassword.setEnabled(false);
        buttonAuthAnonymously.setEnabled(false);

        // Enable buttons based on the authentication methods
        for (AuthenticationMethods authenticationMethod : serverInfo.getAuthenticationMethods()) {
            switch (authenticationMethod) {
                case DISCORD:
                    buttonAuthWithDiscord.setEnabled(true);
                    break;
                case USERNAME_PASSWORD:
                    buttonAuthWithUsernamePassword.setEnabled(true);
                    break;
                case ANONYMOUS:
                    buttonAuthAnonymously.setEnabled(true);
                    break;
            }
        }

        // Enable / Disable previous session token button
        buttonContinueInPreviousSession.setEnabled(false);

        if (serverInfo.isAuthenticationMethodEnabled(AuthenticationMethods.PREVIOUS_SESSION_TOKEN)) {
            var previousSessionToken = SakuyaBridge.INSTANCE.getConfig().getPreviousSessionTokenIfNotExpired();

            if (previousSessionToken != null) {
                buttonContinueInPreviousSession.setEnabled(true);
                buttonContinueInPreviousSession.setText($formatTranslation(Lang.Frames.ServerInfo.BUTTON_CONTINUE_IN_PREVIOUS_SESSION, previousSessionToken.getLoggedAccount().getUsername()));
            }
        } else {
            buttonContinueInPreviousSession.setText($getTranslation(Lang.Frames.ServerInfo.BUTTON_CONTINUE_IN_PREVIOUS_SESSION_DISABLED));
        }
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonContinueInPreviousSession, this::clickContinueInPreviousSession);
        CinnamonRoll.onClick(buttonAuthWithDiscord, this::clickAuthWithDiscord);
        CinnamonRoll.onClick(buttonAuthWithUsernamePassword, this::clickAuthWithUsernamePassword);
        CinnamonRoll.onClick(buttonAuthAnonymously, this::clickAuthAnonymously);
        CinnamonRoll.onClick(buttonDisconnect, e -> clickDisconnect());
    }

    @Override
    public void windowClosing(WindowEvent event) {
        clickDisconnect();
    }

    protected abstract void clickContinueInPreviousSession(MouseEvent mouseEvent);

    protected abstract void clickAuthWithDiscord(MouseEvent mouseEvent);

    protected abstract void clickAuthWithUsernamePassword(MouseEvent mouseEvent);

    protected abstract void clickAuthAnonymously(MouseEvent mouseEvent);

    protected abstract void clickDisconnect();

    /**
     * Prepares the header of the frame.
     */
    private void prepareHeader() {
        JPanel headerPanel = new JPanel(new MigLayout("insets n n 0 n", "[grow]"));

        JLabel serverInfoLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.TEXT_TITLE));
        CinnamonRoll.deriveFontWith(serverInfoLabel, Font.BOLD, 24);
        headerPanel.add(serverInfoLabel, "center, wrap");

        JLabel description = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_DESCRIPTION));
        CinnamonRoll.deriveFontWith(description, Font.PLAIN, 14);
        headerPanel.add(description, "center, wrap");

        headerPanel.add(new JSeparator(), "growx");

        this.add(headerPanel, "growx, wrap");
    }

    private void prepareBody() {
        JPanel bodyPanel = new JPanel(new MigLayout("insets 0 n 0 n", "[grow]"));

        JLabel serverNameLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_SERVER_NAME));
        CinnamonRoll.deriveFontWith(serverNameLabel, Font.BOLD, 12);
        bodyPanel.add(serverNameLabel, "wrap");
        bodyPanel.add(textFieldServerName, "growx, wrap");

        JLabel serverMaintainerLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_SERVER_MAINTAINER));
        CinnamonRoll.makeForegroundDarker(serverMaintainerLabel);
        bodyPanel.add(serverMaintainerLabel, "wrap");
        bodyPanel.add(textFieldServerMaintainer, "growx, wrap");

        JLabel serverRegionLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_SERVER_REGION));
        CinnamonRoll.makeForegroundDarker(serverRegionLabel);
        bodyPanel.add(serverRegionLabel, "wrap");
        bodyPanel.add(textFieldServerRegion, "growx, wrap");

        JLabel serverMotdLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_MOTD));
        CinnamonRoll.makeForegroundDarker(serverMotdLabel);
        bodyPanel.add(serverMotdLabel, "wrap");
        JScrollPane scrollPane = new JScrollPane(textAreaServerMotd);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        bodyPanel.add(scrollPane, "grow, wrap");

        bodyPanel.add(new JSeparator(), "growx");

        this.add(bodyPanel, "growx, wrap");
    }

    private void prepareAuthenticationMethods() {
        JPanel authMethodsPanel = new JPanel(new MigLayout("insets 0 n 0 n", "[grow]"));

        JLabel authMethodsLabel = new JLabel($getTranslation(Lang.Frames.ServerInfo.LABEL_AUTHENTICATE));
        CinnamonRoll.deriveFontWith(authMethodsLabel, Font.BOLD, 16);
        authMethodsPanel.add(authMethodsLabel, "wrap");

        authMethodsPanel.add(buttonContinueInPreviousSession, "growx, wrap");

        authMethodsPanel.add(new JSeparator(), "growx, wrap");

        authMethodsPanel.add(buttonAuthWithUsernamePassword, "growx, wrap");
        authMethodsPanel.add(buttonAuthWithDiscord, "growx, wrap");
        authMethodsPanel.add(buttonAuthAnonymously, "growx, wrap");

        authMethodsPanel.add(new JSeparator(), "growx, wrap");

        // Not really authentication, but it has to be here
        authMethodsPanel.add(buttonDisconnect, "wrap");

        this.add(authMethodsPanel, "growx, wrap");
    }
}
