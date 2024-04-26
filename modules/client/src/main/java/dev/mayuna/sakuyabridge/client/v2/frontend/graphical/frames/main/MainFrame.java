package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main;

import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;

import javax.swing.*;
import java.awt.event.WindowEvent;

public final class MainFrame extends MainFrameDesign {

    public MainFrame() {
    }

    @Override
    public void windowClosing(WindowEvent event) {
        var result = JOptionPane.showConfirmDialog(this, $getTranslation(Lang.Frames.Main.TEXT_CONFIRM_CLOSE), $getTranslation(Lang.Frames.Main.TEXT_TITLE_CONFIRM_CLOSE), JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        // Quit the application
        System.exit(0);
    }
}
