package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.auth.usernamepassword;

import dev.mayuna.cinnamonroll.extension.frames.loading.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.serverinfo.ServerInfoFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.objects.ServerInfo;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Username and password authentication method<br>
 */
@Getter
public final class UsernamePasswordAuthFrame extends UsernamePasswordAuthFrameDesign {

    private final ServerInfo serverInfo;
    private boolean loggedIn;

    public UsernamePasswordAuthFrame(Component parentComponent, ServerInfo serverInfo) {
        super(parentComponent);
        this.serverInfo = serverInfo;

        loadData();
    }

    /**
     * Loads data
     */
    private void loadData() {
        // Disable register button if not enabled
        if (!serverInfo.isRegisterEnabled()) {
            buttonRegister.setEnabled(false);
        }
    }

    @Override
    protected void onCancelClick(MouseEvent mouseEvent) {
        this.dispose();
    }

    @Override
    protected void onLoginClick(MouseEvent mouseEvent) {
        if (!isUsernameAndPasswordValid()) {
            return;
        }

        // Show loading dialog
        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_LOGGING_IN));
        loadingDialog.blockAndShow(this);

        // Get username and password
        String username = fieldUsername.getText();
        char[] password = fieldPassword.getPassword();

        // Login with username and password
        SakuyaBridge.INSTANCE.loginWithUsernameAndPassword(username, password).thenAcceptAsync(result -> {
            loadingDialog.unblockAndClose();

            // Login successful -> set loggedIn to true and dispose frame
            if (result.isSuccessful()) {
                loggedIn = true;
                this.dispose();
                return;
            }

            JOptionPane.showMessageDialog(this, $formatTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_LOGIN_FAILED, result.getErrorMessage()), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    protected void onRegisterClick(MouseEvent mouseEvent) {
        var registerFrame = new RegisterUsernamePasswordAuthFrame(this);

        registerFrame.openFrameBlockParentAndThenAsync(this, () -> {
            if (registerFrame.isRegistered()) {
                loggedIn = true;
                this.dispose();
                return;
            }
        });
    }

    @Override
    public void onEnterPressed(ActionEvent event) {
        onLoginClick(null);
    }
}
