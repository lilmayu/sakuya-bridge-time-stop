package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.auth.usernamepassword;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.CinnamonRollFlatLaf;
import dev.mayuna.sakuyabridge.client.v2.frontend.lang.Lang;
import dev.mayuna.sakuyabridge.commons.v2.CommonConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * Design for username and password authentication method (register)<br>
 */
public abstract class RegisterUsernamePasswordAuthFrameDesign extends UsernamePasswordAuthFrameDesign {

    protected JPasswordField fieldPasswordAgain;

    public RegisterUsernamePasswordAuthFrameDesign(Component parentComponent) {
        super(parentComponent);
    }

    @Override
    protected void prepareComponents() {
        super.prepareComponents();

        this.fieldPasswordAgain = new JPasswordField();
        CinnamonRoll.limitDocumentLength(fieldPasswordAgain, CommonConstants.MAXIMUM_PASSWORD_LENGTH);
        this.fieldPasswordAgain.setFont(CinnamonRoll.createMonospacedFont(12));
        CinnamonRollFlatLaf.showPasswordRevealButton(fieldPasswordAgain, true);

        // If password again is not equal to password or password again is less than minimum password length
        CinnamonRollFlatLaf.addDynamicOutlineError(fieldPasswordAgain, () -> !Arrays.equals(fieldPassword.getPassword(), fieldPasswordAgain.getPassword()) || fieldPasswordAgain.getPassword().length < CommonConstants.MINIMUM_PASSWORD_LENGTH);

        this.buttonLogin.setText($getTranslation(Lang.Frames.Auth.UsernamePassword.BUTTON_REGISTER)); // Replace login with register
        this.buttonRegister.setVisible(false); // Hide previous register button (this button opened this frame)
    }

    @Override
    protected void registerListeners() {
        CinnamonRoll.onClick(buttonLogin, this::onRegisterClick);
        CinnamonRoll.onClick(buttonCancel, this::onCancelClick);
    }

    @Override
    protected void extraBodyHelper(JPanel bodyPanel) {
        bodyPanel.add(new JLabel($getTranslation(Lang.Frames.Auth.UsernamePassword.LABEL_PASSWORD_AGAIN)), "growx, split 2");
        bodyPanel.add(CinnamonRoll.createDynamicCharacterCountLabel(fieldPasswordAgain, true), "wrap");
        bodyPanel.add(fieldPasswordAgain, "growx, wrap");
    }

    @Override
    protected void onLoginClick(MouseEvent mouseEvent) {
        // Empty
    }

    @Override
    protected boolean isUsernameAndPasswordValid() {
        if (!super.isUsernameAndPasswordValid()) {
            return false;
        }

        // Check if password and password again match
        char[] password = fieldPassword.getPassword();
        char[] passwordAgain = fieldPasswordAgain.getPassword();

        if (!Arrays.equals(password, passwordAgain)) {
            JOptionPane.showMessageDialog(this, $getTranslation(Lang.Frames.Auth.UsernamePassword.TEXT_PASSWORD_MISMATCH), $getTranslation(Lang.General.TEXT_ERROR), JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}
