package dev.mayuna.sakuyabridge.client.ui.forms.login;

import dev.mayuna.sakuyabridge.client.Main;
import dev.mayuna.sakuyabridge.client.ui.InfoMessages;
import dev.mayuna.sakuyabridge.client.ui.forms.BaseFormDesign;
import dev.mayuna.sakuyabridge.client.ui.utils.MenuUtils;
import dev.mayuna.sakuyabridge.client.ui.utils.MigLayoutUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class LoginFormDesign extends BaseFormDesign {

    protected JButton btn_usernameAndPassword;
    protected JButton btn_discord;
    protected JButton btn_disconnect;

    public LoginFormDesign(Component parent) {
        super(parent);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Sakuya Bridge: Time Stop");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareTitle();
        prepareLoginMethodsButtons();
        prepareDisconnectButton();
        prepareMenu();

        this.pack();
    }

    @Override
    protected void prepareComponents() {
        btn_usernameAndPassword = new JButton("Username and Password");
        btn_discord = new JButton("Discord");
        btn_disconnect = new JButton("Disconnect");
    }

    @Override
    protected void registerListeners() {
        registerClickListener(btn_usernameAndPassword, this::onUsernameAndPasswordClick);
        registerClickListener(btn_discord, this::onDiscordClick);
        registerClickListener(btn_disconnect, this::onDisconnectClick);
    }

    protected abstract void onUsernameAndPasswordClick(MouseEvent mouseEvent);

    protected abstract void onDiscordClick(MouseEvent mouseEvent);

    private void onDisconnectClick(MouseEvent mouseEvent) {
        var result = InfoMessages.General.DISCONNECT_QUESTION.showQuestion(JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.NO_OPTION) {
            return;
        }

        this.dispose();
        Main.stopConnectionForcefully();
    }

    private void prepareTitle() {
        JLabel title = new JLabel("Login");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        this.add(title, "wrap");

        JLabel subTitle = new JLabel("Please, choose a login method");
        subTitle.setFont(subTitle.getFont().deriveFont(Font.PLAIN, 16f));
        this.add(subTitle, "wrap");

        this.add(new JSeparator(), "growx, wrap");
    }

    private void prepareLoginMethodsButtons() {
        JPanel loginMethods = new JPanel(MigLayoutUtils.createGrow());
        loginMethods.setBorder(BorderFactory.createTitledBorder("Login methods"));

        loginMethods.add(btn_usernameAndPassword, "growx, wrap");
        loginMethods.add(btn_discord, "growx, wrap");

        this.add(loginMethods, "growx, wrap");
    }

    private void prepareDisconnectButton() {
        this.add(btn_disconnect);
    }

    private void prepareMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        MenuUtils.addLoggingWindowToMenu(menu);
        MenuUtils.addDisconnectToMenu(menu);

        this.setJMenuBar(menuBar);
    }
}
