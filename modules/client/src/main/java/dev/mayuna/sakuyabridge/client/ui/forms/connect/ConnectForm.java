package dev.mayuna.sakuyabridge.client.ui.forms.connect;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.configs.ServerConnectConfig;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.loading.LoadingDialogForm;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

public class ConnectForm extends ConnectFormDesign {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ConnectForm.class);

    public ConnectForm(JComponent parent) {
        super(parent);
    }

    @Override
    protected void loadData() {
        ServerConnectConfig config = Main.getConfigs().getServerConnectConfig();
        txt_serverAddress.setText(config.getServerAddress());
    }

    @Override
    protected void onConnectClick(MouseEvent mouseEvent) {
        var loadingDialog = LoadingDialogForm.createConnecting().blockAndShow(this);

        CompletableFuture.runAsync(() -> {
            boolean success = tryConnect(loadingDialog, txt_serverAddress.getText());

            loadingDialog.unblockAndClose();

            if (!success) {
                return;
            }
        });
    }

    @Override
    protected void onOfflineClick(MouseEvent mouseEvent) {
    }

    @Override
    public void onFormClose() {
        ServerConnectConfig config = Main.getConfigs().getServerConnectConfig();
        config.setServerAddress(txt_serverAddress.getText());
    }

    private boolean tryConnect(LoadingDialogForm loadingDialog, String serverAddress) {
        String ip;
        int port;

        // Check IP
        loadingDialog.appendProgressInfo("Checking IP...");

        if (serverAddress.contains(":")) {
            String[] split = serverAddress.split(":");
            ip = split[0];

            try {
                port = Integer.parseInt(split[1]);
            } catch (NumberFormatException exception) {
                InfoMessages.ConnectToServer.INVALID_PORT.showError(loadingDialog);
                return false;
            }

            if (port < 0 || port > 65535) {
                InfoMessages.ConnectToServer.INVALID_PORT.showError(loadingDialog);
                return false;
            }
        } else {
            ip = serverAddress;
            port = NetworkConstants.DEFAULT_PORT;
        }

        LOGGER.mdebug("Connecting to " + ip + " on port " + port + "...");

        // Resolve IP
        loadingDialog.appendProgressInfo("Resolving IP...");

        try {
            ip = InetAddress.getByName(ip).getHostAddress();
        } catch (UnknownHostException exception) {
            LOGGER.error("Failed to resolve IP from " + ip, exception);
            InfoMessages.ConnectToServer.UNKNOWN_HOST.showError(loadingDialog);
            return false;
        }

        // Connect
        loadingDialog.appendProgressInfo(null); // Connecting...

        if (!Main.createConnection(ip, port)) {
            InfoMessages.ConnectToServer.CONNECTION_FAILED.showError(loadingDialog);
            return false;
        }

        // Connected!
        loadingDialog.setProgressInfo("Connected!");

        for (int i = 0; i <= 100; i++) {
            loadingDialog.setProgress(i);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to sleep", e);
            }
        }

        // Exchange version protocol

        return true;
    }
}
