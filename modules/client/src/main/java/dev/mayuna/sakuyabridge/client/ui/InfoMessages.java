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
        public static final Message PROTOCOL_VERSION_EXCHANGE_FAILED = new Message("Protocol version exchange failed");
        public static final Message PROTOCOL_VERSION_MISMATCH_QUESTION = new Message("Server protocol version is %s but client protocol version is %s.\n\nThis can lead to communication errors.\n\nContinue?");
        public static final Message FAILED_TO_GENERATE_ASYMMETRIC_KEY = new Message("Failed to generate asymmetric encryption key");
        public static final Message FAILED_TO_EXCHANGE_ASYMMETRIC_KEY = new Message("Failed to exchange asymmetric encryption key for encrypted symmetric key");
        public static final Message FAILED_TO_DECRYPT_SYMMETRIC_KEY = new Message("Failed to decrypt symmetric encryption key");
        public static final Message FAILED_TO_ENABLE_ENCRYPTED_COMMUNICATION = new Message("Failed to enable encrypted communication");
    }

    /**
     * Represents a message
     */
    public static class Message {

        private String message;

        /**
         * Creates message with a message
         *
         * @param message Message
         */
        public Message(String message) {
            this.message = message;
        }

        /**
         * Formats the message with the given values
         *
         * @param values Values
         */
        public void withValues(Object... values) {
            message = String.format(message, values);
        }

        /**
         * Shows the message as an error
         *
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
         *
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
         *
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

        /**
         * Shows the message as a question
         *
         * @param optionType an int designating the options available on the dialog:
         *                  <code>YES_NO_OPTION</code>,
         *                  <code>YES_NO_CANCEL_OPTION</code>,
         *                  or <code>OK_CANCEL_OPTION</code>
         *
         * @return The answer
         */
        public int showQuestion(Component parentComponent, int optionType) {
            return JOptionPane.showConfirmDialog(parentComponent, message, "Question", optionType);
        }

        /**
         * Shows the message as a question
         *
         * @param optionType an int designating the options available on the dialog:
         *                  <code>YES_NO_OPTION</code>,
         *                  <code>YES_NO_CANCEL_OPTION</code>,
         *                  or <code>OK_CANCEL_OPTION</code>
         *
         * @return The answer
         */
        public int showQuestion(int optionType) {
            return showQuestion(null, optionType);
        }
    }
}
