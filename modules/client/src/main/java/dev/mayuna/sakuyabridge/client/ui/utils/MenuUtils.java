package dev.mayuna.sakuyabridge.client.ui.utils;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.forms.logging.LoggingForm;

import javax.swing.*;

public class MenuUtils {

    /**
     * Adds the logging window to the menu
     *
     * @param menu The menu to add the logging window to
     */
    public static void addLoggingWindowToMenu(JMenu menu) {
        JMenuItem logging = new JMenuItem("Logging Window");
        logging.addActionListener(e -> LoggingForm.getInstance().openForm());
        menu.add(logging);
    }

    /**
     * Adds the exit button to the menu
     *
     * @param menu The menu to add the exit button to
     */
    public static void addExitToMenu(JMenu menu) {
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);
    }

    /**
     * Adds the disconnect button to the menu
     *
     * @param menu The menu to add the disconnect button to
     */
    public static void addDisconnectToMenu(JMenu menu) {
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(e -> {
            var result = InfoMessages.General.DISCONNECT_QUESTION.showQuestion(JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.NO_OPTION) {
                return;
            }

            Main.stopConnectionSafe();
        });
        menu.add(disconnect);
    }
}
