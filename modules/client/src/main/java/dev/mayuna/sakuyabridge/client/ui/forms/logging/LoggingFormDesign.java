package dev.mayuna.sakuyabridge.client.ui.forms.logging;

import dev.mayuna.sakuyabridge.client.ui.forms.BaseFormDesign;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public abstract class LoggingFormDesign extends BaseFormDesign {

    protected JTextArea txtarea_logs;
    private JButton btn_exit;

    public LoggingFormDesign() {
        super(null);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Logging Window");
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        this.setResizable(true);
        this.setLayout(new BorderLayout());

        prepareTitle();
        prepareLogsComponents();
        prepareButtons();
        prepareMenu();

        this.pack();
        this.setSize(800, 600);
    }

    @Override
    protected void prepareComponents() {
        txtarea_logs = new JTextArea();
        btn_exit = new JButton("Exit");
    }

    @Override
    protected void registerListeners() {
        registerClickListener(btn_exit, e -> this.setVisible(false));
    }

    private void prepareTitle() {
        JLabel title = new JLabel("Logging Window");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20.0f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        this.add(title, BorderLayout.NORTH);
    }

    private void prepareLogsComponents() {
        txtarea_logs.setEditable(false);
        txtarea_logs.setBackground(txtarea_logs.getBackground().darker());
        txtarea_logs.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtarea_logs.setLineWrap(true);
        txtarea_logs.setWrapStyleWord(true);

        JScrollPane loggingWindowScrollPane = new JScrollPane(txtarea_logs);
        loggingWindowScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(loggingWindowScrollPane, BorderLayout.CENTER);
    }

    private void prepareButtons() {
        JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel copyLogsButtonPanel = new JPanel(new BorderLayout());

        JButton copyLogsButton = new JButton("Copy Logs");
        copyLogsButton.setOpaque(true);
        copyLogsButton.setBorderPainted(false);
        copyLogsButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(txtarea_logs.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            JOptionPane.showMessageDialog(this, "Logs have been copied to the clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        copyLogsButtonPanel.add(copyLogsButton, BorderLayout.WEST);

        buttonsPanel.add(copyLogsButtonPanel, BorderLayout.WEST);

        JPanel exitButtonPanel = new JPanel(new BorderLayout());

        btn_exit.setOpaque(true);
        btn_exit.setBorderPainted(false);
        exitButtonPanel.add(btn_exit, BorderLayout.EAST);

        buttonsPanel.add(exitButtonPanel, BorderLayout.EAST);

        this.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void prepareMenu() {

    }
}
