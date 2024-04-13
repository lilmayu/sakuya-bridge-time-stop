package dev.mayuna.sakuyabridge.client.v2.frontend.frames.connect;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ConnectFrame extends ConnectFrameDesign {

    @Override
    protected void loadData() {
        labelVersion.setText($formatTranslation(Lang.Frames.Connect.LABEL_VERSION, "2.0", "N/A"));
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
