package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.serverinfo;

import dev.mayuna.cinnamonroll.extension.frames.loading.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.TranslatedInfoMessage;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.auth.usernamepassword.UsernamePasswordAuthFrame;
import dev.mayuna.sakuyabridge.client.v2.frontend.interfaces.GraphicalUserInterface;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;

import javax.swing.*;
import java.awt.event.MouseEvent;

public final class ServerInfoFrame extends ServerInfoFrameDesign {

    public ServerInfoFrame(ServerInfo serverInfo) {
        super(serverInfo);

        // Focus the continue in last session if available
        if (buttonContinueInPreviousSession.isEnabled()) {
            buttonContinueInPreviousSession.requestFocus();
        }
    }

    /**
     * Called when the user has successfully logged in.
     */
    private void successfulLogin() {
        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.ServerInfo.TEXT_FETCHING_CURRENT_USER));
        loadingDialog.blockAndShow(this);

        // Fetch user
        SakuyaBridge.INSTANCE.fetchCurrentUser().thenAcceptAsync(result -> {
            loadingDialog.unblockAndClose();

            if (!result.isSuccessful()) {
                TranslatedInfoMessage.create($formatTranslation(Lang.Frames.ServerInfo.TEXT_FAILED_TO_FETCH_CURRENT_USER, result.getErrorMessage())).showError(this);

                // Disconnect
                SakuyaBridge.INSTANCE.reset();
                this.dispose();
                GraphicalUserInterface.INSTANCE.openConnectFrame();
                return;
            }

            // Open main frame
            this.dispose();
            GraphicalUserInterface.INSTANCE.openMainFrame();
        });
    }

    @Override
    protected void clickContinueInPreviousSession(MouseEvent mouseEvent) {
        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.ServerInfo.TEXT_LOGGING_IN_WITH_SESSION_TOKEN));
        loadingDialog.blockAndShow(this);

        // Login with previous session token
        SakuyaBridge.INSTANCE.loginWithPreviousSession().thenAcceptAsync(result -> {
            loadingDialog.unblockAndClose();

            if (!result.isSuccessful()) {
                JOptionPane.showMessageDialog(this, $formatTranslation(Lang.Frames.ServerInfo.TEXT_FAILED_TO_LOGIN_WITH_SESSION_TOKEN, result.getErrorMessage()), $getTranslation(Lang.Frames.ServerInfo.TEXT_TITLE_FAILED_TO_LOGIN_WITH_SESSION_TOKEN), JOptionPane.ERROR_MESSAGE);
                return;
            }

            successfulLogin();
        });
    }

    @Override
    protected void clickAuthWithDiscord(MouseEvent mouseEvent) {
        // TODO:
    }

    @Override
    protected void clickAuthWithUsernamePassword(MouseEvent mouseEvent) {
        var authFrame = new UsernamePasswordAuthFrame(this, serverInfo);

        authFrame.openFrameBlockParentAndThenAsync(this, () -> {
            if (authFrame.isLoggedIn()) {
                successfulLogin();
                return;
            }
        });
    }

    @Override
    protected void clickAuthAnonymously(MouseEvent mouseEvent) {
        // TODO:
    }

    @Override
    protected void clickDisconnect() {
        var result = JOptionPane.showConfirmDialog(this, $getTranslation(Lang.Frames.ServerInfo.TEXT_CONFIRM_DISCONNECT), $getTranslation(Lang.Frames.ServerInfo.TEXT_TITLE_DISCONNECT), JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        SakuyaBridge.INSTANCE.reset();
        this.dispose();
        GraphicalUserInterface.INSTANCE.openConnectFrame();
    }
}
