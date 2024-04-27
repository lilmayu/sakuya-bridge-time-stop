package dev.mayuna.sakuyabridge.commons.v2.objects.accounts;

import java.util.UUID;

/**
 * Represents an account that is logged in.<br>
 * This is mainly when you do not want to expose other information about the account, e.g., password hash, when sending it to the client.
 */
public final class LoggedAccount extends Account {

    private AccountType accountType;

    /**
     * Used for serialization.
     */
    public LoggedAccount() {
    }

    /**
     * Creates a new account with the given UUID.
     *
     * @param username The username
     * @param uuid     The UUID
     */
    public LoggedAccount(String username, UUID uuid, AccountType accountType) {
        super(username, uuid);
        this.accountType = accountType;
    }

    /**
     * Creates a logged account from an account.
     *
     * @param account The account
     *
     * @return The logged account
     */
    public static LoggedAccount fromAccount(Account account) {
        return new LoggedAccount(account.getUsername(), account.getUuid(), account.getAccountType());
    }

    @Override
    public AccountType getAccountType() {
        return accountType;
    }
}
