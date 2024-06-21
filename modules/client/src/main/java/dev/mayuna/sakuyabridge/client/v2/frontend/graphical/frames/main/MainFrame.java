package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public final class MainFrame extends MainFrameDesign {

    private final Timer pingTimer = new Timer();

    public MainFrame() {
        preparePingTimer();
    }

    /**
     * Prepares the ping timer.
     */
    private void preparePingTimer() {
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    long ping = SakuyaBridge.INSTANCE.getLastPingIn();
                    String textPing = String.valueOf(ping);

                    if (ping == -1) {
                        textPing = "<1";
                    }

                    labelPing.setText($formatTranslation(Lang.Frames.Main.LABEL_PING, textPing));
                });
            }
        }, 0, CommonConstants.PING_INTERVAL);
    }

    /**
     * Updates the username label with the current username.
     */
    private void updateUsernameLabel() {
        labelLoggedAs.setText($formatTranslation(Lang.Frames.Main.LABEL_LOGGED_AS, SakuyaBridge.INSTANCE.getUser().getUsername()));
    }

    @Override
    protected void populateData() {
        super.populateData();

        updateUsernameLabel();
    }

    @Override
    protected void clickDisconnect(MouseEvent mouseEvent) {
        var result = TranslatedInfoMessage.create($getTranslation(Lang.Frames.Main.TEXT_DO_YOU_WISH_TO_DISCONNECT)).showQuestion(this, JOptionPane.YES_NO_OPTION);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        SakuyaBridge.INSTANCE.reset();
        this.dispose();
        GraphicalUserInterface.INSTANCE.openConnectFrame();
    }

    @Override
    public void windowClosing(WindowEvent event) {
        var result = JOptionPane.showConfirmDialog(this, $getTranslation(Lang.Frames.Main.TEXT_CONFIRM_CLOSE), $getTranslation(Lang.Frames.Main.TEXT_TITLE_CONFIRM_CLOSE), JOptionPane.YES_NO_OPTION);

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        // Quit the application
        System.exit(0);
    }

    @Override
    public void dispose() {
        pingTimer.cancel(); // Cancel the ping timer

        super.dispose();
    }
}
