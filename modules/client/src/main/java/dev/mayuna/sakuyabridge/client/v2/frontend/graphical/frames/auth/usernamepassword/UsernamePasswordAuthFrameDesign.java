package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.auth.usernamepassword;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.BaseSakuyaBridgeFrameDesign;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Design for username and password authentication method<br>
 * Also allows for {@link RegisterUsernamePasswordAuthFrameDesign} to add "password again" field for password confirmation using
 * {@link #extraBodyHelper(JPanel)}
 */
public abstract class UsernamePasswordAuthFrameDesign extends BaseSakuyaBridgeFrameDesign {

    protected JTextField fieldUsername;
    protected JPasswordField fieldPassword;

    protected JButton buttonLogin;
    protected JButton buttonRegister;
    protected JButton buttonCancel;

    public UsernamePasswordAuthFrameDesign(Component parentComponent) {
        super(parentComponent);
    }

    @Override
    protected void prepareFrame(Component parentComponent) {
        this.setTitle($getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_TITLE));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(MigLayoutUtils.createGrow());

        prepareBody();

        this.pack();
        this.setMinimumSize(new Dimension(300, this.getHeight()));
        this.setLocationRelativeTo(parentComponent);
    }

    @Override
    protected void prepareComponents() {
        this.fieldUsername = new JTextField();
        this.fieldPassword = new JPasswordField();

        this.buttonLogin = new JButton($getTranslation(Lang.Frames.Auth.UsernamePassword.BUTTON_LOGIN));
        this.buttonRegister = new JButton($getTranslation(Lang.Frames.Auth.UsernamePassword.BUTTON_NO_ACCOUNT_REGISTER));
        this.buttonCancel = new JButton($getTranslation(Lang.Frames.Auth.UsernamePassword.BUTTON_CANCEL));

        CinnamonRoll.limitDocumentLength(fieldUsername, CommonConstants.MAXIMUM_USERNAME_LENGTH);
        CinnamonRoll.limitDocumentLength(fieldPassword, CommonConstants.MAXIMUM_PASSWORD_LENGTH);

        CinnamonRollFlatLaf.addDynamicOutlineError(fieldUsername, () -> fieldUsername.getText().length() < CommonConstants.MINIMUM_USERNAME_LENGTH);
        CinnamonRollFlatLaf.addDynamicOutlineError(fieldPassword, () -> fieldPassword.getPassword().length < CommonConstants.MINIMUM_PASSWORD_LENGTH);

        this.fieldUsername.setFont(CinnamonRoll.createMonospacedFont(12));
        this.fieldPassword.setFont(CinnamonRoll.createMonospacedFont(12));

        CinnamonRollFlatLaf.showPasswordRevealButton(fieldPassword, true);
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonCancel, this::onCancelClick);
        CinnamonRoll.onClick(buttonLogin, this::onLoginClick);

        buttonRegister.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRegisterClick(null);
            }
        });

        //CinnamonRoll.onClick(buttonRegister, this::onRegisterClick);
    }

    protected abstract void onCancelClick(MouseEvent mouseEvent);

    protected abstract void onLoginClick(MouseEvent mouseEvent);

    protected abstract void onRegisterClick(MouseEvent mouseEvent);

    protected void prepareBody() {
        JPanel bodyPanel = new JPanel(MigLayoutUtils.createGrow());

        bodyPanel.add(new JLabel($getTranslation(Lang.Frames.Auth.UsernamePassword.LABEL_USERNAME)), "growx, split 2");
        bodyPanel.add(CinnamonRoll.createDynamicCharacterCountLabel(fieldUsername, true), "wrap");
        bodyPanel.add(fieldUsername, "growx, wrap");

        bodyPanel.add(new JLabel($getTranslation(Lang.Frames.Auth.UsernamePassword.LABEL_PASSWORD)), "growx, split 2");
        bodyPanel.add(CinnamonRoll.createDynamicCharacterCountLabel(fieldPassword, true), "wrap");
        bodyPanel.add(fieldPassword, "growx, wrap");

        extraBodyHelper(bodyPanel);

        bodyPanel.add(new JSeparator(), "growx, wrap");

        bodyPanel.add(buttonRegister, "split 3");
        bodyPanel.add(new JLabel(), "growx");
        bodyPanel.add(buttonLogin, "wrap");

        bodyPanel.add(new JSeparator(), "growx, wrap");

        bodyPanel.add(buttonCancel);

        this.add(bodyPanel, "grow");
    }

    /**
     * Ability to add another password field for password confirmation.
     *
     * @param bodyPanel The body panel to add the confirmation password field to.
     */
    protected void extraBodyHelper(JPanel bodyPanel) {
    }

    /**
     * Checks if the username and password are valid.<br>
     * This should be in a {@link UsernamePasswordAuthFrame}, but since it will be also used (and overriden) by
     * {@link RegisterUsernamePasswordAuthFrame}, it is here.<br>
     *
     * @return {@code true} if the username and password are valid, {@code false} otherwise.
     */
    protected boolean isUsernameAndPasswordValid() {
        // Username is empty
        if (fieldUsername.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, $getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_USERNAME_EMPTY), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Username is too short
        if (fieldUsername.getText().length() < CommonConstants.MINIMUM_USERNAME_LENGTH) {
            JOptionPane.showMessageDialog(this, $formatTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_USERNAME_SHORT, CommonConstants.MINIMUM_USERNAME_LENGTH), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        char[] password = fieldPassword.getPassword();

        // Password is empty
        if (password.length == 0) {
            JOptionPane.showMessageDialog(this, $getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_PASSWORD_EMPTY), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Password is too short
        if (password.length < CommonConstants.MINIMUM_PASSWORD_LENGTH) {
            JOptionPane.showMessageDialog(this, $formatTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_PASSWORD_SHORT, CommonConstants.MINIMUM_PASSWORD_LENGTH), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void onEscapePressed(ActionEvent event) {
        dispose();
    }
}
