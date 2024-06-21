package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.TabbedPanel;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.AccountType;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;

import javax.swing.*;
import java.awt.*;

public class AccountTabbedPanel extends TabbedPanel {

    private JTabbedPane tabbedPane;
    private JTextField textFieldAccountUsername;
    private JTextField textFieldAccountUuid;
    private JTextField textFieldAccountAccountType;
    private JButton buttonChangeAccountUsername;
    private JButton buttonTransferToUsernamePasswordAccountType;
    private JButton buttonDeleteAccount;

    public AccountTabbedPanel() {
        super(new BorderLayout());

        prepareComponents();
        populateData();

        prepareTabs();
    }

    private void prepareComponents() {
        tabbedPane = new JTabbedPane();

        textFieldAccountUsername = new JTextField();
        textFieldAccountUuid = new JTextField();
        textFieldAccountAccountType = new JTextField();

        textFieldAccountUsername.setEditable(false);
        textFieldAccountUuid.setEditable(false);
        textFieldAccountAccountType.setEditable(false);

        buttonChangeAccountUsername = new JButton("Change Username");
        buttonTransferToUsernamePasswordAccountType = new JButton("Transfer to Username Password Account");
        buttonDeleteAccount = new JButton("Delete Account");

        buttonChangeAccountUsername.setEnabled(false); // Allowed for every other account than anonymous
        buttonTransferToUsernamePasswordAccountType.setEnabled(false); // Enabled only for account that are not username/password and anonymous

        // TODO: Implement listeners
    }

    private void populateData() {
        LoggedAccount loggedAccount = SakuyaBridge.INSTANCE.getLoggedAccount();

        // Should not happen, but just in case
        if (loggedAccount == null) {
            return;
        }

        textFieldAccountUsername.setText(loggedAccount.getUsername());
        textFieldAccountUuid.setText(loggedAccount.getUuid().toString());
        textFieldAccountAccountType.setText(loggedAccount.getAccountType().name()); // TODO: Translation based on enum name

        AccountType accountType = loggedAccount.getAccountType();

        if (accountType != AccountType.USERNAME_PASSWORD && accountType != AccountType.ANONYMOUS) {
            buttonTransferToUsernamePasswordAccountType.setEnabled(true);
        }

        if (accountType != AccountType.ANONYMOUS) {
            buttonChangeAccountUsername.setEnabled(true);
        }
    }

    private void prepareTabs() {
        tabbedPane.addTab("Account", createAccountPanel());
        tabbedPane.addTab("User", createUserPanel());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAccountPanel() {
        JPanel accountPanel = new JPanel(MigLayoutUtils.createGrow());

        var usernameLabel = new JLabel("Username");
        CinnamonRoll.makeForegroundDarker(usernameLabel);
        accountPanel.add(usernameLabel);

        var uuidLabel = new JLabel("UUID");
        CinnamonRoll.makeForegroundDarker(uuidLabel);
        accountPanel.add(uuidLabel);

        var accountTypeLabel = new JLabel("Account Type");
        CinnamonRoll.makeForegroundDarker(accountTypeLabel);
        accountPanel.add(accountTypeLabel, "wrap");

        accountPanel.add(textFieldAccountUsername, "grow");
        accountPanel.add(textFieldAccountUuid, "grow");
        accountPanel.add(textFieldAccountAccountType, "grow, wrap");

        accountPanel.add(buttonChangeAccountUsername, "wrap");
        accountPanel.add(buttonTransferToUsernamePasswordAccountType, "wrap");
        accountPanel.add(buttonDeleteAccount, "wrap");

        return accountPanel;
    }

    private JPanel createUserPanel() {
        JPanel userPanel = new JPanel(MigLayoutUtils.createGrow());

        userPanel.add(new JLabel("User Statistics (now empty)"));

        return userPanel;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }
}
