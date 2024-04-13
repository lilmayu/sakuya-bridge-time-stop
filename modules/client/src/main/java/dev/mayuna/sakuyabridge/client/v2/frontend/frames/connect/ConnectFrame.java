package dev.mayuna.sakuyabridge.client.v2.frontend.frames.connect;

import dev.mayuna.cinnamonroll.extension.LoadingDialogFrame;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ConnectFrame extends ConnectFrameDesign {

    @Override
    protected void loadData() {
        labelVersion.setText($formatTranslation(Lang.Frames.Connect.LABEL_VERSION, "2.0", "N/A"));
    }

    @Override
    protected void clickConnect(MouseEvent mouseEvent) {
        var loadingDialog = new LoadingDialogFrame("Connecting...");
        loadingDialog.blockAndShow(this);

        SakuyaBridge.INSTANCE.boot();

        SakuyaBridge.INSTANCE.connectToServer(serverAddressField.getText()).thenAccept(success -> {
            loadingDialog.unblockAndClose();

            if (success) {
                openLoggingFrame(mouseEvent);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to connect to the server. Check logs!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    protected void openLoggingFrame(MouseEvent mouseEvent) {
        JDialog dialog = new JDialog(this, "Logging", true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
