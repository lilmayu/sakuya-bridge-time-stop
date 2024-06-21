package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.auth.usernamepassword;

import dev.mayuna.cinnamonroll.extension.frames.loading.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Username and password authentication method (register)<br>
 */
@Getter
public final class RegisterUsernamePasswordAuthFrame extends RegisterUsernamePasswordAuthFrameDesign {

    /**
     * Whether the user has successfully registered.
     */
    private boolean registered;

    public RegisterUsernamePasswordAuthFrame(Component parentComponent) {
        super(parentComponent);
    }

    @Override
    protected void onCancelClick(MouseEvent mouseEvent) {
        dispose();
    }

    @Override
    protected void onRegisterClick(MouseEvent mouseEvent) {
        if (!isUsernameAndPasswordValid()) {
            return;
        }

        var loadingDialog = new LoadingDialogFrame($getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_REGISTERING));
        loadingDialog.blockAndShow(this);

        String username = fieldUsername.getText();
        char[] password = fieldPassword.getPassword();

        // Register with username and password
        SakuyaBridge.INSTANCE.registerWithUsernameAndPassword(username, password).thenAcceptAsync(result -> {
            loadingDialog.unblockAndClose();

            // Register successful -> set registered to true and dispose frame
            if (result.isSuccessful()) {
                registered = true;
                this.dispose();
                return;
            }

            JOptionPane.showMessageDialog(this, $formatTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_REGISTER_FAILED, result.getErrorMessage()), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void onEnterPressed(ActionEvent event) {
        onRegisterClick(null);
    }
}
