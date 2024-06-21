package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.account;

import dev.mayuna.cinnamonroll.CinnamonRoll;
import dev.mayuna.cinnamonroll.util.MigLayoutUtils;
import dev.mayuna.sakuyabridge.client.v2.frontend.graphical.components.SakuyaBridgeTabbedPanelDesign;

import javax.swing.*;
import java.awt.*;

/**
 * The design of the account tabbed panel.
 */
public abstract class AccountTabbedPanelDesign extends SakuyaBridgeTabbedPanelDesign {

    protected JTabbedPane tabbedPane;
    protected JTextField textFieldAccountUsername;
    protected JTextField textFieldAccountUuid;
    protected JTextField textFieldAccountAccountType;
    protected JButton buttonChangeAccountUsername;
    protected JButton buttonTransferToUsernamePasswordAccountType;
    protected JButton buttonDeleteAccount;

    public AccountTabbedPanelDesign() {
        super(new BorderLayout());
        loadData();
    }

    @Override
    protected void prepareComponents() {
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
    }

    @Override
    protected void registerListeners() {
        // TODO: Implement listeners
    }

    @Override
    protected void populatePanel() {
        tabbedPane.addTab("Account", createAccountPanel());
        tabbedPane.addTab("User", createUserPanel());

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Loads the data for the account tabbed panel
     */
    protected abstract void loadData();

    // TODO: Move to its own class
    /**
     * Creates the account panel
     * @return The account panel
     */
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
}
