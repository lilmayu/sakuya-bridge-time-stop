package dev.mayuna.sakuyabridge.client.ui.forms;

import dev.mayuna.sakuyabridge.client.ui.utils.SimpleClickMouseListener;
import dev.mayuna.sakuyabridge.client.ui.utils.SimpleTextFieldChangedValueListener;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public abstract class BaseFormDesign extends JFrame {

    private final Object closeMutex = new Object();

    public BaseFormDesign(Component parent) {
        super();
        loadData();
        prepareComponents();
        prepare(parent);
        registerListeners();
    }

    /**
     * Loads data
     */
    protected abstract void loadData();

    /**
     * Prepares the user interface
     */
    protected abstract void prepare(Component parent);

    /**
     * Prepares the components
     */
    protected void prepareComponents() {
    }

    /**
     * Registers listeners
     */
    protected abstract void registerListeners();

    // Other

    /**
     * Registers a click listener for a button
     *
     * @param button   the button
     * @param consumer the consumer
     */
    public void registerClickListener(JButton button, Consumer<MouseEvent> consumer) {
        SimpleClickMouseListener.register(button, consumer);
    }

    /**
     * Registers a text changed listener for a text field
     *
     * @param textField the text field
     * @param consumer  the consumer
     */
    public void registerTextChangedListener(JTextField textField, Consumer<DocumentEvent> consumer) {
        SimpleTextFieldChangedValueListener.register(textField, consumer);
    }

    /**
     * Registers a value changed listener for a combo box
     *
     * @param comboBox the combo box
     * @param consumer the consumer
     * @param <T>      the type of the combo box
     */
    public <T> void registerComboBoxValueChangedListener(JComboBox<T> comboBox, Consumer<ItemEvent> consumer) {
        comboBox.addItemListener(consumer::accept);
    }

    /**
     * Registers a check box checked listener
     *
     * @param checkBox the check box
     * @param consumer the consumer
     */
    public void registerCheckBoxCheckedListener(JCheckBox checkBox, Consumer<ActionEvent> consumer) {
        checkBox.addActionListener(consumer::accept);
    }

    /**
     * Waits until the form is closed.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    @SneakyThrows
    public void waitUntilClosed() {
        synchronized (closeMutex) {
            closeMutex.wait();
        }
    }

    /**
     * Opens the form
     */
    public void openForm() {
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();

        synchronized (closeMutex) {
            closeMutex.notifyAll();
        }
    }
}
