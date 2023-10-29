package dev.mayuna.sakuyabridge.client.ui.forms.login.usernameandpassword;

import dev.mayuna.sakuyabridge.client.ui.forms.BaseFormDesign;
import dev.mayuna.sakuyabridge.client.ui.utils.DocumentLengthLimit;
import dev.mayuna.sakuyabridge.client.ui.utils.MigLayoutUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class UsernameAndPasswordLoginFormDesign extends BaseFormDesign {

    protected JTextField txt_username;
    protected JPasswordField txt_password;
    protected JButton btn_register;
    protected JButton btn_login;
    protected JButton btn_cancel;

    public UsernameAndPasswordLoginFormDesign(Component parent) {
        super(parent);
    }

    @Override
    protected void prepare(Component parent) {
        this.setTitle("Username and Password login");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareBody();
        prepareButtons();

        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    protected void prepareComponents() {
        txt_username = new JTextField();
        txt_username.setDocument(new DocumentLengthLimit(16));
        txt_password = new JPasswordField(); // TODO: Eye icon to show password utils
        txt_password.setDocument(new DocumentLengthLimit(32));
        btn_register = new JButton("Register");
        btn_login = new JButton("  Login  ");
        btn_cancel = new JButton("Cancel");
    }

    @Override
    protected void registerListeners() {
        registerClickListener(btn_cancel, this::onCancelClick);
        registerClickListener(btn_login, this::onLoginClick);
        registerClickListener(btn_register, this::onRegisterClick);
    }

    protected abstract void onLoginClick(MouseEvent mouseEvent);

    protected abstract void onRegisterClick(MouseEvent mouseEvent);

    protected void onCancelClick(MouseEvent mouseEvent) {
        this.dispose();
    }

    private void prepareBody() {
        var panel = new JPanel();
        panel.setLayout(MigLayoutUtils.create("[shrink][grow]"));

        panel.add(new JLabel("Username:"));
        panel.add(txt_username, "growx, wrap");

        panel.add(new JLabel("Password:"));
        panel.add(txt_password, "growx");

        this.add(panel, "growx, wrap");
    }

    private void prepareButtons() {
        var panel = new JPanel();
        panel.setLayout(MigLayoutUtils.create("[shrink][grow][shrink][shrink]"));

        panel.add(btn_cancel);
        panel.add(new JLabel(), "growx");
        panel.add(btn_register);
        panel.add(btn_login);

        this.add(panel);
    }
}
