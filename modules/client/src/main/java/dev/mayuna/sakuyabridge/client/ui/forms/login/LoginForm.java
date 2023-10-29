package dev.mayuna.sakuyabridge.client.ui.forms.login;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.networking.tcp.NetworkTask;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.forms.login.usernameandpassword.UsernameAndPasswordLoginForm;
import dev.mayuna.sakuyabridge.client.ui.loading.LoadingDialogForm;
import dev.mayuna.sakuyabridge.commons.login.LoginMethod;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class LoginForm extends LoginFormDesign {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(LoginForm.class);

    public LoginForm() {
        super(null);
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void onFormOpen() {
        LoadingDialogForm loadingDialog = LoadingDialogForm.createFetchingLoginMethods().blockAndShow(this);

        CompletableFuture.runAsync(() -> {
            boolean success = fetchLoginMethods(loadingDialog);
            loadingDialog.unblockAndClose();

            if (!success) {
                this.dispose();
                Main.stopConnectionSafe();
                Main.openConnectForm();
                return;
            }
        });
    }

    @Override
    protected void onUsernameAndPasswordClick(MouseEvent mouseEvent) {
        var form = new UsernameAndPasswordLoginForm(this);
        form.openForm();

        this.setEnabled(false);
        CompletableFuture.runAsync(() -> {
            form.waitUntilClosed();
            this.setEnabled(true);
            this.requestFocus();
        });
    }

    @Override
    protected void onDiscordClick(MouseEvent mouseEvent) {
        // TODO
    }

    private boolean fetchLoginMethods(LoadingDialogForm loadingDialog) {
        // Send request
        Packets.LoginMethodsResponse loginMethodsResponse;

        try {
            loginMethodsResponse = new NetworkTask.FetchLoginMethods().run().join();
        } catch (Exception exception) {
            LOGGER.error("Failed to fetch login methods", exception);
            InfoMessages.LoginMethods.FAILED_TO_FETCH_LOGIN_METHODS.showError(loadingDialog);
            return false;
        }

        if (loginMethodsResponse.hasError()) {
            LOGGER.error("Failed to fetch login methods: " + loginMethodsResponse.getErrorMessage());
            InfoMessages.LoginMethods.FAILED_TO_FETCH_LOGIN_METHODS.showError(loadingDialog);
            return false;
        }

        LOGGER.info("Server supports the following login methods: " + Arrays.toString(loginMethodsResponse.getLoginMethods()));

        btn_usernameAndPassword.setEnabled(false);
        btn_discord.setEnabled(false);

        for (LoginMethod loginMethod : loginMethodsResponse.getLoginMethods()) {
            switch (loginMethod) {
                case USERNAME_PASSWORD:
                    btn_usernameAndPassword.setEnabled(true);
                    break;
                case DISCORD:
                    btn_discord.setEnabled(true);
                    break;
            }
        }

        return true;
    }
}
