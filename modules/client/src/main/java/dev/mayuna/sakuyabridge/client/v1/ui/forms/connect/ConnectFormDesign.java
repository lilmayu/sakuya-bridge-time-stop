package dev.mayuna.sakuyabridge.client.v1.ui.forms.connect;

import dev.mayuna.sakuyabridge.client.v1.ui.forms.BaseFormDesign;
import dev.mayuna.sakuyabridge.client.v1.ui.utils.MenuUtils;
import dev.mayuna.sakuyabridge.client.v1.ui.utils.MigLayoutUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class ConnectFormDesign extends BaseFormDesign {

    protected JTextField txt_serverAddress;
    protected JButton btn_exit;
    protected JButton btn_offline;
    protected JButton btn_connect;

    public ConnectFormDesign(Component parent) {
        super(parent);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Sakuya Bridge: Time Stop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareTitle();
        prepareConnectSettings();
        prepareButtons();
        prepareMenu();

        this.pack();
        this.setSize(340, this.getHeight());
        this.setLocationRelativeTo(parent);
    }

    @Override
    protected void prepareComponents() {
        txt_serverAddress = new JTextField();
        btn_exit = new JButton("Exit");
        btn_offline = new JButton("Offline");
        btn_connect = new JButton("  Connect  ");
    }

    @Override
    protected void registerListeners() {
        registerClickListener(btn_exit, e -> System.exit(0));
        registerClickListener(btn_offline, this::onOfflineClick);
        registerClickListener(btn_connect, this::onConnectClick);
    }

    protected abstract void onOfflineClick(MouseEvent mouseEvent);

    protected abstract void onConnectClick(MouseEvent mouseEvent);

    private void prepareTitle() {
        JLabel title = new JLabel("Sakuya Bridge: Time Stop");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        this.add(title, "wrap");

        JLabel subtitle = new JLabel("Connect to server");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 16f));
        this.add(subtitle, "wrap");

        this.add(new JSeparator(), "growx, wrap");
    }

    private void prepareConnectSettings() {
        JPanel settingsPanel = new JPanel(MigLayoutUtils.createGrow());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        JLabel serverAddressLabel = new JLabel("Server address");
        settingsPanel.add(serverAddressLabel, "wrap");
        settingsPanel.add(txt_serverAddress, "growx, wrap");

        this.add(settingsPanel, "growx, wrap");
    }

    private void prepareButtons() {
        JPanel buttons = new JPanel(MigLayoutUtils.createNoInsets("[shrink][grow][shrink][shrink]"));

        buttons.add(btn_exit);
        buttons.add(new JLabel(), "growx");
        buttons.add(btn_offline);
        buttons.add(btn_connect);

        this.add(buttons, "growx, wrap");
    }

    private void prepareMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        MenuUtils.addLoggingWindowToMenu(menu);
        MenuUtils.addExitToMenu(menu);

        this.setJMenuBar(menuBar);
    }
}
