package dev.mayuna.sakuyabridge.client.ui.forms.connect;

import dev.mayuna.sakuyabridge.client.ui.forms.BaseFormDesign;
import dev.mayuna.sakuyabridge.client.ui.forms.logging.LoggingForm;
import dev.mayuna.sakuyabridge.client.ui.utils.MigLayoutUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class ConnectFormDesign extends BaseFormDesign {

    protected JTextField txt_serverAddress;
    protected JButton btn_exit;
    protected JButton btn_connect;

    public ConnectFormDesign(Component parent) {
        super(parent);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Sakuya Bridge: Time Stop");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareTitle();
        prepareServerAddress();
        prepareButtons();
        prepareMenu();

        this.pack();
        this.setSize(340, this.getHeight());
    }

    @Override
    protected void prepareComponents() {
        txt_serverAddress = new JTextField();
        btn_exit = new JButton("Exit");
        btn_connect = new JButton("Connect");
    }

    @Override
    protected void registerListeners() {
        registerClickListener(btn_exit, e -> System.exit(0));
        registerClickListener(btn_connect, this::onConnectClick);
    }

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

    private void prepareServerAddress() {
        JLabel serverAddressLabel = new JLabel("Server address");
        this.add(serverAddressLabel, "wrap");
        this.add(txt_serverAddress, "growx, wrap");
    }

    private void prepareButtons() {
        JPanel buttons = new JPanel(MigLayoutUtils.createNoInsets("[shrink][grow][shrink]"));

        buttons.add(btn_exit);
        buttons.add(new JLabel(), "growx");
        buttons.add(btn_connect);

        this.add(buttons, "growx, wrap");
    }

    private void prepareMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        JMenuItem logging = new JMenuItem("Logging Window");
        logging.addActionListener(e -> LoggingForm.getInstance().openForm());
        menu.add(logging);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

        this.setJMenuBar(menuBar);
    }
}
