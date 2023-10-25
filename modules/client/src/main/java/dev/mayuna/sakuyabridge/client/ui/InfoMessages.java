package dev.mayuna.sakuyabridge.client.ui;

import dev.mayuna.sakuyabridge.commons.jacoco.Generated;

import javax.swing.*;
import java.awt.*;

@Generated
public class InfoMessages {

    public static class ConnectToServer {

        public final static Message UNKNOWN_HOST = new Message("Unknown host (check server address)");
        public static final Message INVALID_PORT = new Message("Invalid port (0 < port < 65535)");
        public static final Message CONNECTION_FAILED = new Message("Connection failed (check logs for details)");
        public static final Message CONNECTION_LOST = new Message("Connection lost (check logs for details)");
    }

    /**
     * Represents a message
     */
    public static class Message {

        private final String message;

        /**
         * Creates message with a message
         *
         * @param message Message
         */
        public Message(String message) {
            this.message = message;
        }

        /**
         * Shows the message as an error
         * @param parentComponent Parent component
         */
        public void showError(Component parentComponent) {
            JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
        }

        /**
         * Shows the message as an error
         */
        public void showError() {
            showError(null);
        }

        /**
         * Shows the message as a warning
         * @param parentComponent Parent component
         */
        public void showWarning(Component parentComponent) {
            JOptionPane.showMessageDialog(parentComponent, message, "Warning", JOptionPane.WARNING_MESSAGE);
        }

        /**
         * Shows the message as a warning
         */
        public void showWarning() {
            showWarning(null);
        }

        /**
         * Shows the message as an info
         * @param parentComponent Parent component
         */
        public void showInfo(Component parentComponent) {
            JOptionPane.showMessageDialog(parentComponent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        /**
         * Shows the message as an info
         */
        public void showInfo() {
            showInfo(null);
        }
    }
}
