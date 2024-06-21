package dev.mayuna.sakuyabridge.client.v2.frontend.graphical.frames.main.panels.account;

import dev.mayuna.sakuyabridge.client.v2.backend.SakuyaBridge;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.AccountType;
import dev.mayuna.sakuyabridge.commons.v2.objects.accounts.LoggedAccount;

public final class AccountTabbedPanel extends AccountTabbedPanelDesign {

    public AccountTabbedPanel() {
    }

    @Override
    protected void loadData() {
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

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose() {

    }
}
