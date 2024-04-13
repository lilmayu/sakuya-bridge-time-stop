package dev.mayuna.sakuyabridge.client.v1.ui.utils;

import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.function.Consumer;

public class SimpleTextFieldChangedValueListener implements DocumentListener {

    private final @Getter Consumer<DocumentEvent> onValueChanged;

    public SimpleTextFieldChangedValueListener(@NonNull Consumer<DocumentEvent> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    /**
     * Registers a {@link SimpleTextFieldChangedValueListener} to a {@link JTextField}.
     *
     * @param textField      The {@link JTextField} to register the listener to.
     * @param onValueChanged The {@link Consumer} to call when the value of the {@link JTextField} changes.
     */
    public static void register(JTextField textField, @NonNull Consumer<DocumentEvent> onValueChanged) {
        textField.getDocument().addDocumentListener(new SimpleTextFieldChangedValueListener(onValueChanged));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        onValueChanged.accept(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        onValueChanged.accept(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        onValueChanged.accept(e);
    }
}
