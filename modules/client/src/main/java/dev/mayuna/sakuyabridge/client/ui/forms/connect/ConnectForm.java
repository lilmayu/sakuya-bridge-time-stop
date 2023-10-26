package dev.mayuna.sakuyabridge.client.ui.forms.connect;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.configs.ServerConnectConfig;
import dev.mayuna.sakuyabridge.client.networking.NetworkTask;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.loading.LoadingDialogForm;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;

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
                Main.stopConnection();
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
        loadingDialog.setProgressInfo("Exchanging data...");
        loadingDialog.appendProgressInfo("Protocol version");

        Packets.ProtocolVersionExchange serverProtocolVersion;

        try {
            serverProtocolVersion = new NetworkTask.ExchangeProtocolVersion().runSync(NetworkConstants.COMMUNICATION_PROTOCOL_VERSION);
        } catch (Exception exception) {
            LOGGER.error("Failed to exchange protocol version", exception);
            InfoMessages.ConnectToServer.PROTOCOL_VERSION_EXCHANGE_FAILED.showError(loadingDialog);
            return false;
        }

        if (serverProtocolVersion.getProtocolVersion() != NetworkConstants.COMMUNICATION_PROTOCOL_VERSION) {
            LOGGER.warn("Server protocol version is " + serverProtocolVersion.getProtocolVersion() + " but client protocol version is " + NetworkConstants.COMMUNICATION_PROTOCOL_VERSION);

            if (InfoMessages.ConnectToServer.PROTOCOL_VERSION_MISMATCH_QUESTION.showQuestion(JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return false;
            }

            LOGGER.warn("Ignoring protocol version mismatch");
        }

        // Generate RSA Key pair
        loadingDialog.appendProgressInfo("Encrypting...");

        try {
            LOGGER.mdebug("Generating asymmetric key pair... (" + EncryptionManager.ASYMMETRIC_KEY_TYPE + ")");
            Main.getEncryptionManager().generateAsymmetricKeyPair();
            LOGGER.mdebug("Generated asymmetric key pair (" + EncryptionManager.ASYMMETRIC_KEY_TYPE + ")");
        } catch (Exception exception) {
            LOGGER.error("Failed to generate asymmetric key pair", exception);
            InfoMessages.ConnectToServer.FAILED_TO_GENERATE_ASYMMETRIC_KEY.showError(loadingDialog);
            return false;
        }

        // Exchange asymmetric key
        Packets.SymmetricKeyExchange encryptedSymmetricKey;

        try {
            encryptedSymmetricKey = new NetworkTask.ExchangeAsymmetricKey().runSync(Main.getEncryptionManager().getAsymmetricPublicKey());
        } catch (Exception exception) {
            LOGGER.error("Failed to exchange asymmetric key for encrypted symmetric key", exception);
            InfoMessages.ConnectToServer.FAILED_TO_EXCHANGE_ASYMMETRIC_KEY.showError(loadingDialog);
            return false;
        }

        if (encryptedSymmetricKey.hasError()) {
            LOGGER.error("Failed to exchange asymmetric key for encrypted symmetric key: " + encryptedSymmetricKey.getErrorMessage());
            InfoMessages.ConnectToServer.FAILED_TO_EXCHANGE_ASYMMETRIC_KEY.showError(loadingDialog);
            return false;
        }

        // Decrypt symmetric key
        try {
            byte[] decryptedSymmetricKey = Main.getEncryptionManager().decryptUsingAsymmetricKey(encryptedSymmetricKey.getEncryptedSymmetricKey());
            Main.getEncryptionManager().setSymmetricKeyFromBytes(decryptedSymmetricKey);
        } catch (Exception exception) {
            LOGGER.error("Failed to decrypt symmetric key", exception);
            InfoMessages.ConnectToServer.FAILED_TO_DECRYPT_SYMMETRIC_KEY.showError(loadingDialog);
            return false;
        }

        LOGGER.mdebug("Symmetric key was exchanged successfully");

        return true;
    }
}
