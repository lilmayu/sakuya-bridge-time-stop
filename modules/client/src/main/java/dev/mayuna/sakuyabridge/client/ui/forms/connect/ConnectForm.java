package dev.mayuna.sakuyabridge.client.ui.forms.connect;

import dev.mayuna.sakuyabridge.client.Main;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ConnectForm extends ConnectFormDesign {

    private @Setter Consumer<String> onConnectToServer = ip -> {};

    public ConnectForm(JComponent parent) {
        super(parent);
    }

    @Override
    protected void onConnectClick(MouseEvent mouseEvent) {
        onConnectToServer.accept(txt_serverAddress.getText());
    }

    @Override
    protected void loadData() {
    }
}
