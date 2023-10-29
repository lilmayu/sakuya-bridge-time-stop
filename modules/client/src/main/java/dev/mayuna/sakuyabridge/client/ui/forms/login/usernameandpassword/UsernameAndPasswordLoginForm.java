package dev.mayuna.sakuyabridge.client.ui.forms.login.usernameandpassword;

import dev.mayuna.sakuyabridge.client.ui.forms.login.LoginResultGetter;

import java.awt.*;
import java.awt.event.MouseEvent;

public class UsernameAndPasswordLoginForm extends UsernameAndPasswordLoginFormDesign implements LoginResultGetter {

    private boolean loggedIn;

    public UsernameAndPasswordLoginForm(Component parent) {
        super(parent);
    }

    @Override
    protected void onLoginClick(MouseEvent mouseEvent) {

    }

    @Override
    protected void onRegisterClick(MouseEvent mouseEvent) {

    }

    @Override
    protected void loadData() {

    }

    @Override
    public boolean loggedIn() {
        return loggedIn;
    }
}
